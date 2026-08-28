export function formatMoney(value: number | string) {
  const amount = typeof value === 'string' ? Number(value) : value
  return `৳${amount.toLocaleString('en-BD', { maximumFractionDigits: 0 })}`
}

export function primaryImage(images?: { url: string; primary: boolean }[]) {
  return images?.find((img) => img.primary)?.url || images?.[0]?.url || ''
}
