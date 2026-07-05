export type User = {
  id: number;
  name: string;
  email: string;
  role: string;
};

export type AccountRequest = {
  id: number;
  systemName: string;
  status: string;
  reason: string;
  reviewComment: string | null;
};