import { Link } from 'react-router-dom'

export function Pagination({
  page,
  totalPages,
  onPage,
}: {
  page: number
  totalPages: number
  onPage: (page: number) => void
}) {
  if (totalPages <= 1) return null
  return (
    <div className="mt-12 flex items-center justify-center gap-3">
      <button
        type="button"
        disabled={page <= 0}
        onClick={() => onPage(page - 1)}
        className="border border-line px-4 py-2 text-sm disabled:opacity-40"
      >
        Previous
      </button>
      <span className="text-sm text-muted">
        {page + 1} / {totalPages}
      </span>
      <button
        type="button"
        disabled={page + 1 >= totalPages}
        onClick={() => onPage(page + 1)}
        className="border border-line px-4 py-2 text-sm disabled:opacity-40"
      >
        Next
      </button>
    </div>
  )
}

export function SectionHeading({ eyebrow, title, to }: { eyebrow: string; title: string; to?: string }) {
  return (
    <div className="mb-8 flex items-end justify-between gap-4">
      <div>
        <p className="text-xs uppercase tracking-[0.22em] text-copper">{eyebrow}</p>
        <h2 className="mt-2 font-display text-3xl md:text-4xl">{title}</h2>
      </div>
      {to && (
        <Link to={to} className="hidden text-sm tracking-wide underline underline-offset-4 md:inline">
          View all
        </Link>
      )}
    </div>
  )
}
