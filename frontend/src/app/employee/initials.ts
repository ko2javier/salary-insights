// "Ana Lopez" → "AL", for the round avatar next to each employee.
export function initials(fullName: string): string {
  return fullName
    .split(' ')
    .filter((word) => word.length > 0)
    .slice(0, 2)
    .map((word) => word[0].toUpperCase())
    .join('');
}
