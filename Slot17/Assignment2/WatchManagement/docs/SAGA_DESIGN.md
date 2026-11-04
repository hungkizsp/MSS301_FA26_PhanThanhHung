# Saga Design Watch Management

MSWatch orchestrates local transactions in DBWatch and MSBrand owns BrandWatches in DBBrand.

| Saga | Steps | Compensation |
| --- | --- | --- |
| Create | create watch, assign brand | unassign brand, delete watch |
| Update | update watch, assign new brand | restore snapshot, assign old brand |
| Delete | unassign brand, delete watch | assign old brand; delete is pivot |

States are `PENDING`, `EXECUTING`, `COMPLETED`, `FAILED`, `COMPENSATING`, and `COMPENSATED`.
Compensations execute LIFO, are idempotent, and retry at most three times. The orchestrator must not span HTTP calls with `@Transactional`; each service commits its own local transaction. 2PC is unsuitable because it locks independent services and REST participants do not join an XA transaction.
