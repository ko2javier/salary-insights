// Fixed lists the backend knows: countries come from V2__insert_countries.sql, departments and job
// titles from the Department and JobTitle enums. The API has no endpoint for them, so they live here.

export const COUNTRIES = [
  { code: 'US', name: 'United States' },
  { code: 'ES', name: 'Spain' },
  { code: 'DE', name: 'Germany' },
  { code: 'GB', name: 'United Kingdom' },
  { code: 'CN', name: 'China' },
  { code: 'IN', name: 'India' },
  { code: 'MX', name: 'Mexico' },
];

export const DEPARTMENTS = [
  'Engineering',
  'Sales',
  'Operations',
  'Marketing',
  'Finance',
  'Human Resources',
];

export const JOB_TITLES = [
  'Software Engineer',
  'Senior Software Engineer',
  'Engineering Manager',
  'Account Executive',
  'Sales Manager',
  'Operations Analyst',
  'Operations Manager',
  'Marketing Specialist',
  'Marketing Manager',
  'Financial Analyst',
  'Finance Manager',
  'HR Specialist',
  'HR Manager',
];
