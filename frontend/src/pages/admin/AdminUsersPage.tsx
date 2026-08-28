import { useEffect, useState } from 'react'
import { adminUpdateUserStatus, adminUsers } from '../../api/shop'
import type { UserSummary } from '../../types'

export function AdminUsersPage() {
  const [users, setUsers] = useState<UserSummary[]>([])

  function load() {
    adminUsers().then(setUsers)
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div>
      <h1 className="font-display text-4xl">Users</h1>
      <div className="mt-6 overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-line">
              <th className="py-2">Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} className="border-b border-line">
                <td className="py-3">
                  {user.firstName} {user.lastName}
                </td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>{user.status}</td>
                <td>
                  <button
                    type="button"
                    className="underline"
                    onClick={async () => {
                      await adminUpdateUserStatus(user.id, user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')
                      load()
                    }}
                  >
                    {user.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
