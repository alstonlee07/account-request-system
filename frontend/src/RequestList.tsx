import type { User, AccountRequest } from './types.ts'
import { useState } from 'react'

type RequestListProps = {
  requests: AccountRequest[];
  currentUser: User;
  statusFilter: string;
  setStatusFilter: (value: string) => void;
  handleApprove: (requestId: number) => void;
  handleReject: (requestId: number, comment: string) => void;
  handleCancel: (requestId: number) => void;
};

function RequestList({ requests, currentUser, statusFilter, setStatusFilter, handleApprove, handleReject, handleCancel }: RequestListProps) {
    const [rejectingId, setRejectingId] = useState<number | null>(null);
    const [rejectComment, setRejectComment] = useState('');
  
    return (
    <div>
        <h2>申請列表</h2>
        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">全部</option>
            <option value="PENDING">PENDING</option>
            <option value="APPROVED">APPROVED</option>
            <option value="REJECTED">REJECTED</option>
            <option value="CANCELLED">CANCELLED</option>
        </select>
        <table>
            <thead>
                <tr>
                <th>系統名稱</th>
                <th>狀態</th>
                <th>申請原因</th>
                <th>審核意見</th>
                <th>操作</th>
                </tr>
            </thead>
            <tbody>
                {requests.map(request => (
                <tr key={request.id}>
                    <td>{request.systemName}</td>
                    <td>
                    <span className={`status status-${request.status.toLowerCase()}`}>{request.status}</span>
                    </td>
                    <td>{request.reason}</td>
                    <td>{request.reviewComment || '-'}</td>
                    <td>
                        {currentUser.role === 'MANAGER' && request.status === 'PENDING' && rejectingId !== request.id && (
                            <>
                            <button className="approve-btn" onClick={() => handleApprove(request.id)}>核准</button>
                            <button className="reject-btn" onClick={() => setRejectingId(request.id)}>拒絕</button>
                            </>
                        )}

                        {rejectingId === request.id && (
                            <>
                            <input
                                type="text"
                                className="text-input"
                                placeholder="拒絕原因"
                                value={rejectComment}
                                onChange={(e) => setRejectComment(e.target.value)}
                            />
                            <button
                                className="reject-btn"
                                onClick={() => {
                                handleReject(request.id, rejectComment);
                                setRejectingId(null);
                                setRejectComment('');
                                }}
                            >
                                確認拒絕
                            </button>
                            <button onClick={() => setRejectingId(null)}>取消</button>
                            </>
                        )}

                        {currentUser.role === 'EMPLOYEE' && request.status === 'PENDING' && (
                            <button className="cancel-btn" onClick={() => handleCancel(request.id)}>取消</button>
                        )}
                    </td>
                </tr>
                ))}
            </tbody>
        </table>
    </div>
  );
}

export default RequestList;