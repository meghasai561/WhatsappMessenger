import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const api = axios.create({})

export default function App(){
  const [users,setUsers] = useState([])
  const [me,setMe] = useState(null)
  const [conversations,setConversations] = useState([])
  const [current,setCurrent] = useState(null)
  const [messages,setMessages] = useState([])
  const [text,setText] = useState('')
  const [file,setFile] = useState(null)
  const stompRef = useRef(null)

  useEffect(()=>{ (async ()=>{
    const u = await api.get('/users'); setUsers(u.data)
  })()},[])

  function createUser(){
    const username = prompt('username?')
    const displayName = prompt('display name?')
    api.post('/users',{username,displayName}).then(res=>setMe(res.data))
  }

  async function createConversation(){
    const type = window.confirm('OK=GROUP, Cancel=DIRECT')?'GROUP':'DIRECT'
    const idsStr = prompt('Participant user IDs comma-separated (include yourself if created):')
    const ids = idsStr.split(',').map(s=>parseInt(s.trim())).filter(Boolean)
    const title = type==='GROUP'? prompt('Group title?') : null
    const res = await api.post('/conversations',{type,title,participantUserIds:ids})
    setConversations(prev=>[...prev,res.data])
  }

  async function loadConversations(){
    const res = await api.get('/conversations'); setConversations(res.data)
  }

  function connectWS(){
    const sock = new SockJS('http://localhost:8080/ws')
    const stomp = new Client({ webSocketFactory: () => sock, reconnectDelay: 2000 })
    stomp.onConnect = () => {
      console.log('WS connected')
      if(current){
        stomp.subscribe('/topic/conversations.'+current.id, (msg)=>{
          const dto = JSON.parse(msg.body)
          setMessages(prev => [...prev, dto])
        })
      }
    }
    stomp.activate()
    stompRef.current = stomp
  }

  async function openConversation(c){
    setCurrent(c)
    const res = await api.get(`/conversations/${c.id}/messages`)
    setMessages(res.data)
    if(stompRef.current){
      try{ stompRef.current.deactivate() }catch{}
    }
    connectWS()
  }

  async function send(){
    if(!me){ alert('Create/select a user first'); return }
    let attachmentId = null
    if(file){
      const form = new FormData()
      form.append('file', file)
      const upload = await api.post('/media', form, { headers: { 'Content-Type':'multipart/form-data' } })
      attachmentId = upload.data.id
    }
    const payload = {
      clientMsgId: crypto.randomUUID(),
      body: text,
      contentType: file ? (file.type.startsWith('image')?'IMAGE': file.type.startsWith('video')?'VIDEO':'AUDIO') : 'TEXT',
      attachmentId,
      e2ee: false
    }
    const res = await api.post(`/conversations/${current.id}/messages?senderUserId=${me.id}`, payload)
    setMessages(prev => [...prev, res.data])
    setText(''); setFile(null)
  }

  function forward(msg){
    if(!current){return}
    const convId = prompt('Forward to conversation id?')
    if(!convId) return
    api.post(`/conversations/${convId}/messages?senderUserId=${me.id}`, {
      clientMsgId: crypto.randomUUID(),
      body: msg.body,
      contentType: msg.contentType,
      attachmentId: msg.attachmentId,
      originalMessageId: msg.id
    })
  }

  return (
    <div style={{display:'grid',gridTemplateColumns:'240px 1fr', height:'100vh', fontFamily:'system-ui'}}>
      <div style={{borderRight:'1px solid #ddd', padding:12}}>
        <h3>Users</h3>
        <button onClick={createUser}>Create User</button>
        <ul>
          {users.map(u=>(
            <li key={u.id} style={{cursor:'pointer', color: me?.id===u.id?'green':'#333'}}
                onClick={()=>setMe(u)}>
              {u.id}. {u.username}
            </li>
          ))}
        </ul>
        <hr/>
        <button onClick={loadConversations}>Load Conversations</button>
        <button onClick={createConversation}>New Conversation</button>
        <ul>
          {conversations.map(c=>(
            <li key={c.id} style={{cursor:'pointer'}} onClick={()=>openConversation(c)}>
              {c.id}. {c.type} {c.title||''}
            </li>
          ))}
        </ul>
      </div>
      <div style={{display:'flex',flexDirection:'column'}}>
        <div style={{padding:12, borderBottom:'1px solid #ddd'}}>
          <b>Current:</b> {current? (current.id+' '+current.type+' '+(current.title||'')) : 'None'}
          <span style={{float:'right'}}>{me? 'Me: '+me.username: 'Pick/Create a user'}</span>
        </div>
        <div style={{flex:1, overflow:'auto', padding:12, background:'#fafafa'}}>
          {messages.map(m=>(
            <div key={m.id} style={{margin:'8px 0', maxWidth:'70%', padding:8, borderRadius:8, background: me && m.senderUserId===me.id ? '#dcf8c6':'#fff', border:'1px solid #eee'}}>
              <div style={{fontSize:12, color:'#888'}}>#{m.id} • {m.contentType}{m.originalMessageId? ` (fwd #${m.originalMessageId})`:''}</div>
              {m.contentType==='TEXT' && <div>{m.body}</div>}
              {m.attachmentId && (
                <div style={{marginTop:6}}>
                  <a href={`/media/${m.attachmentId}`} target="_blank">Open attachment</a>
                </div>
              )}
              <div style={{fontSize:11, color:'#aaa'}}>status: {m.status}</div>
              <button style={{marginTop:6}} onClick={()=>forward(m)}>Forward</button>
            </div>
          ))}
        </div>
        <div style={{padding:12, borderTop:'1px solid #ddd'}}>
          <input style={{width:'60%'}} value={text} onChange={e=>setText(e.target.value)} placeholder="Type a message"/>
          <input type="file" onChange={e=>setFile(e.target.files[0])} style={{marginLeft:8}}/>
          <button onClick={send} style={{marginLeft:8}}>Send</button>
        </div>
      </div>
    </div>
  )
}
