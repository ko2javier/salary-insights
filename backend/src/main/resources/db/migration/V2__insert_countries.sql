-- Fixed exchange rates, set once (docs/decisions/0001-seed-data.md).
-- Source: Federal Reserve, H.10 release of 2026-10-26, rates as of 2026-10-23.
-- https://www.federalreserve.gov/releases/h10/
-- The Fed quotes EUR and GBP as USD per unit, and CNY, INR and MXN as units per
-- USD; those three are stored inverted, so converting is always salary * usd_per_unit.

INSERT INTO country (code, name, currency, usd_per_unit) VALUES
    ('US', 'United States',  'USD', 1),
    ('ES', 'Spain',          'EUR', 1.1199),
    ('DE', 'Germany',        'EUR', 1.1199),
    ('GB', 'United Kingdom', 'GBP', 1.3375),
    ('CN', 'China',          'CNY', 0.149921),  -- 1 / 6.6702
    ('IN', 'India',          'INR', 0.010533),  -- 1 / 94.94
    ('MX', 'Mexico',         'MXN', 0.053580);  -- 1 / 18.6638
