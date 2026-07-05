import type { User } from './types.ts'

type UserSwitcherProps = {
  users: User[];
  currentUser: User | null;
  onSelect: (user: User) => void;
};

function UserSwitcher({ users, currentUser, onSelect }: UserSwitcherProps) {
  return (
    <ul>
      {users.map(user => (
        <li
          key={user.id}
          className={`user-item ${user.id === currentUser?.id ? 'selected' : ''}`}
          onClick={() => onSelect(user)}>
          {user.name}
        </li>
      ))}
    </ul>
  );
}
export default UserSwitcher;