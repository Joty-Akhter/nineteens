import { type FormEvent, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { getErrorMessage } from '../api/client'
import { useAuth } from '../auth'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from || '/'
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      const response = await login(email, password)
      navigate(response.user.role === 'ADMIN' && from === '/' ? '/admin' : from)
    } catch (err) {
      setError(getErrorMessage(err, 'Could not sign in'))
    }
  }

  return (
    <div className="mx-auto max-w-md px-4 py-16">
      <h1 className="font-display text-4xl">Welcome back</h1>
      <p className="mt-2 text-sm text-muted">Demo: user@nineteens.com / User@123 or admin@nineteens.com / Admin@123</p>
      <form onSubmit={(e) => void onSubmit(e)} className="mt-8 grid gap-4">
        <input className="border border-line px-3 py-3" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input className="border border-line px-3 py-3" placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <p className="text-sm text-copper">{error}</p>}
        <button className="bg-ink py-3 text-paper">Login</button>
      </form>
      <p className="mt-4 text-sm">
        New here? <Link to="/register" className="underline">Create an account</Link>
      </p>
    </div>
  )
}

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', firstName: '', lastName: '', phone: '' })
  const [error, setError] = useState('')

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      await register(form)
      navigate('/')
    } catch (err) {
      setError(getErrorMessage(err, 'Could not register'))
    }
  }

  return (
    <div className="mx-auto max-w-md px-4 py-16">
      <h1 className="font-display text-4xl">Create an account</h1>
      <form onSubmit={(e) => void onSubmit(e)} className="mt-8 grid gap-4">
        <input className="border border-line px-3 py-3" placeholder="First name" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
        <input className="border border-line px-3 py-3" placeholder="Last name" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} />
        <input className="border border-line px-3 py-3" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input className="border border-line px-3 py-3" placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
        <input className="border border-line px-3 py-3" placeholder="Password (min 8 characters)" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        {error && <p className="text-sm text-copper">{error}</p>}
        <button className="bg-ink py-3 text-paper">Register</button>
      </form>
    </div>
  )
}
