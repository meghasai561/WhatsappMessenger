import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const api = axios.create({baseURL: import.meta.env.VITE_API_BASE})

export default function App(){
  const [users, setUsers] = useState([])
  const [me, setMe] = useState(null)
  const [conversations, setConversations] = useState([])
  const [current, setCurrent] = useState(null)
  const [messages, setMessages] = useState([])
  const [text, setText] = useState('')
  const [file, setFile] = useState(null)
  const stompRef = useRef(null)
  const currentConvRef = useRef(null)

  useEffect(()=>{ (async ()=>{
    try {
      const u = await api.get('users')
      setUsers(u.data)
    } catch(e) {
      console.error('Failed to load users', e)
    }
  })()},[])

  async function createUser(){
    const username = prompt('username? (e.g. alice)')
    if(!username) return
    const displayName = prompt('display name? (optional)') || username
    try {
      const res = await api.post('users', { username, displayName })
      setMe(res.data)
      setUsers(prev => [...prev, res.data])
    } catch(e){
      alert('Create user failed: '+ (e?.response?.data || e.message))
    }
  }

  async function createConversation(){
    const isGroup = window.confirm('OK = GROUP, Cancel = DIRECT')
    const idsStr = prompt('Participant user IDs comma-separated (include your user id)')
    if(!idsStr) return
    const ids = idsStr.split(',').map(s=>parseInt(s.trim())).filter(Boolean)
    const type = isGroup ? 'GROUP' : 'DIRECT'
    try {
      const res = await api.post('api/conversations', { type, participantUserIds: ids, title: isGroup?prompt('Group title?'):null })
      setConversations(prev => [...prev, res.data])
    } catch(e) {
      alert('Create conversation failed: '+ (e?.response?.data || e.message))
    }
  }

  async function loadConversations(){
    try {
      const res = await api.get('api/conversations')
      setConversations(res.data)
    } catch(e) {
      console.error('Load convs failed', e)
    }
  }

  function connectWS(convId){
    // keep current conv in ref for onMessage handler
    currentConvRef.current = convId
    if(stompRef.current){
      try{ stompRef.current.deactivate() } catch(e){ /* ignore */ }
    }
    const sock = new SockJS('/ws')
    const stomp = new Client({ webSocketFactory: () => sock, reconnectDelay: 2000 })
    stomp.onConnect = () => {
      // subscribe to topic for this conversation
      stomp.subscribe('/topic/conversations.'+convId, (msg)=>{
        try {
          const dto = JSON.parse(msg.body)
          // append message if it's for current conversation
          setMessages(prev => [...prev, dto])
        } catch(e) {
          console.error('Invalid message payload', e)
        }
      })
    }
    stomp.onStompError = (err) => console.error('STOMP error', err)
    stomp.activate()
    stompRef.current = stomp
  }

  async function openConversation(c){
    setCurrent(c)
    try {
      const res = await api.get(`api/conversations/${c.id}/messages`)
      setMessages(res.data)
      connectWS(c.id)
    } catch(e){
      console.error('Failed to load messages', e)
    }
  }

  async function send(){
    if(!me){ alert('Create/select a user first'); return }
    if(!current){ alert('Open a conversation first'); return }

    let attachmentId = null
    if(file){
      const form = new FormData()
      form.append('file', file)
      try {
        const upload = await api.post('api/media', form, { headers: { 'Content-Type':'multipart/form-data' } })
        attachmentId = upload.data.id
      } catch(e){
        alert('File upload failed: '+ (e?.response?.data || e.message))
        return
      }
    }

    const payload = {
      clientMsgId: crypto.randomUUID ? crypto.randomUUID() : (Date.now()+'-'+Math.random()),
      body: text,
      contentType: file ? (file.type.startsWith('image')?'IMAGE': file.type.startsWith('video')?'VIDEO':'AUDIO') : 'TEXT',
      attachmentId,
      e2ee: false
    }

    try {
      const res = await api.post(`api/conversations/${current.id}/messages?senderUserId=${me.id}`, payload)
      setMessages(prev => [...prev, res.data])
      setText('')
      setFile(null)
    } catch(e){
      alert('Send failed: '+ (e?.response?.data || e.message))
    }
  }

  async function forward(msg){
    if(!me){ alert('Set a user first'); return }
    const convId = prompt('Forward to conversation id?')
    if(!convId) return
    try {
      await api.post(`api/conversations/${convId}/messages?senderUserId=${me.id}`, {
        clientMsgId: crypto.randomUUID ? crypto.randomUUID() : (Date.now()+'-'+Math.random()),
        body: msg.body,
        contentType: msg.contentType,
        attachmentId: msg.attachmentId,
        originalMessageId: msg.id
      })
      alert('Forwarded')
    } catch(e){
      alert('Forward failed: '+ (e?.response?.data || e.message))
    }
  }

  return (
    <div style={{display:'grid',gridTemplateColumns:'260px 1fr', height:'100vh'}}>
      <div style={{borderRight:'1px solid #ddd', padding:12, overflow:'auto'}}>
        <h3>Users</h3>
        <div style={{display:'flex', gap:8, marginBottom:8}}>
          <button onClick={createUser}>Create User</button>
          <button onClick={()=>{ if(users.length) setMe(users[0]) }}>Quick select</button>
        </div>
        <div style={{marginBottom:12}}>
          <b>Me:</b> {me ? `${me.id} • ${me.username}` : 'none'}
        </div>
        <ul>
          {users.map(u=>(
            <li key={u.id} style={{cursor:'pointer', padding:'6px 0', color: me?.id===u.id ? 'green' : '#222'}} onClick={()=>setMe(u)}>
              {u.id}. {u.username} <small style={{color:'#666'}}>({u.displayName})</small>
            </li>
          ))}
        </ul>

        <hr />
        <div style={{display:'flex', gap:8}}>
          <button onClick={loadConversations}>Load</button>
          <button onClick={createConversation}>New</button>
        </div>

        <ul style={{marginTop:8}}>
          {conversations.map(c=>(
            <li key={c.id} style={{cursor:'pointer', padding:'6px 0'}} onClick={()=>openConversation(c)}>
              {c.id}. {c.type} {c.title ? `- ${c.title}` : ''}
            </li>
          ))}
        </ul>
      </div>

      <div style={{display:'flex',flexDirection:'column'}}>
        <div style={{padding:12, borderBottom:'1px solid #ddd'}}>
          <b>Conversation:</b> {current ? `${current.id} • ${current.type}` : 'none'}
          <span style={{float:'right'}}>{me ? `Logged in: ${me.username}` : 'Not set' }</span>
        </div>

        <div style={{flex:1, overflow:'auto', padding:12, background:'#fafafa'}}>
          {messages.map(m=>(
            <div key={m.id || Math.random()} style={{
                margin:'8px 0', maxWidth:'70%', padding:10, borderRadius:8,
                background: me && m.senderUserId === me.id ? '#dcf8c6' : '#fff',
                border:'1px solid #eee'
              }}>
              <div style={{fontSize:12, color:'#666'}}>#{m.id} • {m.contentType}{m.originalMessageId? ` (fwd #${m.originalMessageId})`:''}</div>
              {m.contentType === 'TEXT' && <div style={{marginTop:6}}>{m.body}</div>}
              {m.attachmentId && <div style={{marginTop:8}}><a href={`/media/${m.attachmentId}`} target="_blank">Open attachment</a></div>}
              <div style={{fontSize:11,color:'#999', marginTop:8}}>status: {m.status || 'SENT'}</div>
              <button style={{marginTop:8}} onClick={()=>forward(m)}>Forward</button>
            </div>
          ))}
        </div>

        <div style={{padding:12, borderTop:'1px solid #ddd', display:'flex', gap:8, alignItems:'center'}}>
          <input style={{flex:1, padding:8}} value={text} onChange={e=>setText(e.target.value)} placeholder="Type a message" />
          <input type="file" onChange={e=>setFile(e.target.files[0])} />
          <button onClick={send}>Send</button>
        </div>
      </div>
    </div>
  )
}
