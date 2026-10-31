# 01 — Requirements

**Date:** 2026-10-28 → 2026-10-31
**Tool:** Claude (chat, then Claude Code)
**Output:** `REQUIREMENTS.md`

Prompts are kept verbatim, in Spanish as I wrote them, with an English translation below each one.

---

## Session 1 — first draft

### Prompt 1

> dime tu el draft .. lo revisamos aca o me das el .md y desde alli seguimos trabajando

*Give me the draft. Do we review it here, or do you give me the .md and we keep working from there?*

**AI response (summary):** before writing, it asked me to decide two things:
1. **Currency.** One currency (simpler, recorded as an assumption) or local currency per country (more realistic, but then a global average is meaningless without conversion).
2. **The HR Manager's questions.** It pointed out that this section is where the judgment is, and that if it is generic, the rest will be too.

It also reminded me that the brief asks to keep the AI prompts, and that this conversation already counts.

### Prompt 2

> 1- una moneda. un normalizador por col en dependencia del pais
> 2- Ni idea ?

*1. One currency, with a per-country normalizer. 2. No idea?*

**My decision:** salaries stay in each country's local currency, and every country carries a fixed rate that converts them to one reporting currency. That keeps the data realistic and still makes cross-country figures comparable.

On the questions, I had no list of my own, so I asked the AI to propose one.

### Prompt 3

> creame el requiremts completo y lo revisamos juntos

*Write the full requirements and we'll review them together.*

**AI response (summary):** a complete first draft, built around six questions the HR Manager must be able to answer, with every feature in scope traced back to one of them, and an out-of-scope list with a reason for each item.

---

## Session 2 — review

### Prompt 4

> revisa el requierements y haslo de nuevo si es necesario..

*Review the requirements and redo them if needed.*

**AI response (summary):** it kept the structure and most of the text, and corrected rather than rewrote. Two real defects:

1. **Currency was only solved for Q5, but four questions need it.** A department spans several countries, so min/max/average per department (Q3), payroll per department (Q4) and sorting by salary without a country filter (Q6) would add up different currencies. Fix: any figure that mixes countries is shown in USD; figures within one country in local currency and USD.
2. **"Unit tests on the summary logic" contradicted "calculated in SQL".** Mocking the repository would not test the query, which is the part that can be wrong. Fix: summary queries are tested against a small fixed dataset in a throwaway SQLite database, where the expected figures can be worked out by hand.

Additions:
- Conversion is applied **per employee, before aggregating** (averaging first and converting after is wrong for any group spanning countries).
- Job title and department validated against the fixed list, so free text can't split one group into two.
- Unique email, hire date not in the future.
- Salaries stored as integers in whole currency units: exact sums, exact test assertions.
- A measurable performance target (under 300 ms) instead of "respond quickly".
- Test-first stated explicitly.

It also flagged a technical risk the document doesn't cover: Hibernate has no official SQLite dialect (`hibernate-community-dialects` is needed), to be checked in the first step.

**What I decided:** _(fill in: what I kept, changed or rejected after reading it)_
