import './App.css'
import type { User, AccountRequest } from './types.ts'
import { useState, useEffect } from 'react'
import UserSwitcher from './UserSwitcher.tsx'
import NewRequestForm from './NewRequestForm.tsx'
import RequestList from './RequestList.tsx'

function App() {
  const [users, setUsers] = useState<User[]>([]);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [requests, setRequests] = useState<AccountRequest[]>([]);
  const [systemName, setSystemName] = useState('');
  const [reason, setReason] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState('');

  useEffect(() => {
    fetch('http://localhost:8080/api/users')
      .then(response => response.json())
      .then(data => setUsers(data));
  }, []);

  useEffect(() => {
    if (!currentUser) return;
    loadRequests();
  }, [currentUser, statusFilter]);

  function loadRequests() {
    if (!currentUser) return;

    const url = statusFilter
      ? `http://localhost:8080/api/requests?status=${statusFilter}`
      : 'http://localhost:8080/api/requests';

    fetch(url, {
      headers: { 'X-User-Id': String(currentUser.id) }
    })
      .then(response => response.json().then(data => ({ ok: response.ok, data })))
      .then(({ ok, data }) => {
        if (!ok) {
          setError(data.message);
          return;
        }
        setError(null);
        setRequests(data);
      });
  }

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!currentUser) return;

    fetch('http://localhost:8080/api/requests', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': String(currentUser.id)
      },
      body: JSON.stringify({ systemName, reason })
    })
      .then(response => response.json().then(data => ({ ok: response.ok, data })))
      .then(({ ok, data }) => {
        if (!ok) {
          setError(data.message);
          return;
        }
        setError(null);
        setSystemName('');
        setReason('');
        loadRequests();
      });
  }

  function handleApprove(id: number) {
    if (!currentUser) return;

    fetch(`http://localhost:8080/api/requests/${id}/approve`, {
      method: 'PATCH',
      headers: { 'X-User-Id': String(currentUser.id) }
    })
      .then(response => response.json().then(data => ({ ok: response.ok, data })))
      .then(({ ok, data }) => {
        if (!ok) {
          setError(data.message);
          return;
        }
        setError(null);
        loadRequests();
      });
  }

  function handleReject(id: number, comment: string) {
    if (!currentUser) return;

    fetch(`http://localhost:8080/api/requests/${id}/reject`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': String(currentUser.id)
      },
      body: JSON.stringify({ comment })
    })
      .then(response => response.json().then(data => ({ ok: response.ok, data })))
      .then(({ ok, data }) => {
        if (!ok) {
          setError(data.message);
          return;
        }
        setError(null);
        loadRequests();
      });
  }

  function handleCancel(id: number) {
    if (!currentUser) return;

    fetch(`http://localhost:8080/api/requests/${id}/cancel`, {
      method: 'PATCH',
      headers: { 'X-User-Id': String(currentUser.id) }
    })
      .then(response => response.json().then(data => ({ ok: response.ok, data })))
      .then(({ ok, data }) => {
        if (!ok) {
          setError(data.message);
          return;
        }
        setError(null);
        loadRequests();
      });
  }

  return (
    <div>
      <h1>Account Request System</h1>
      <p>目前身份:{currentUser ? currentUser.name : '尚未選擇'}</p>
      <UserSwitcher users={users} currentUser={currentUser} onSelect={setCurrentUser} />

      {error && <p className="error-message">{error}</p>}

      <NewRequestForm
        systemName={systemName}
        reason={reason}
        setSystemName={setSystemName}
        setReason={setReason}
        handleSubmit={handleSubmit}
      />

      {currentUser && (
        <RequestList
          requests={requests}
          currentUser={currentUser}
          handleApprove={handleApprove}
          handleReject={handleReject}
          handleCancel={handleCancel}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}  
        />
      )}
    </div>
  );
}

export default App