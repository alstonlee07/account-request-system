type NewRequestFormProps = {
  systemName: string;
  reason: string;
  setSystemName: (value: string) => void;
  setReason: (value: string) => void;
  handleSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
};

function NewRequestForm({ systemName, reason, setSystemName, setReason, handleSubmit }: NewRequestFormProps) {
  return (
    <div>
      <h2>新增申請</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          className="text-input"
          placeholder="系統名稱"
          value={systemName}
          onChange={(e) => setSystemName(e.target.value)}
        />
        <input
          type="text"
          className="text-input"
          placeholder="申請原因"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
        />
        <button type="submit" className="submit-btn">
          送出申請
        </button>
      </form>
    </div>
  );
}

export default NewRequestForm;