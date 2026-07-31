---
title: Proxy
short: Proxy
order: 70
desc: A stand-in for another object, used to control access to it.
---

# Proxy

<div class="definition"><span class="lbl">Definition</span>
The Proxy Pattern provides a <strong>representative or placeholder for another object</strong> in order to <strong>control access to it</strong>.
</div>

The proxy implements the same interface as the real object, so it can be dropped in anywhere the real one is expected — and the client never learns which it is holding.

## What it's used for

| Use | What the proxy does | Common name |
| --- | --- | --- |
| **Access control** | Checks permissions before forwarding | Protection proxy |
| **Performance** | Defers expensive creation until first real use | Virtual proxy |
| **Pre/post processing** | Adds logging, auditing, metrics around the call | Smart proxy |
| **Caching** | Returns a stored result instead of re-computing | Caching proxy |
| **Remoting** | Hides the fact the real object is on another machine | Remote proxy |

The common thread: something must happen **around** the call, and neither the client nor the real object should have to know about it.

## Implementation

An employee DAO with role-based access control:

```java-sample dir="lld/patterns/structural/proxy" variant="good" title="Protection proxy" files="EmployeeDo.java,EmployeeDao.java,EmployeeDaoImpl.java,EmployeeDaoProxy.java,EmployeeManagement.java" run
```

Two details do the work:

1. **The proxy implements `EmployeeDao`** — the same interface as the real subject. That is what makes it substitutable.
2. **The client declares `EmployeeDao`**, not `EmployeeDaoProxy`. Swap in the real object, or a caching proxy, or a logging proxy, and not one line of client code changes.

`EmployeeDaoImpl` contains no permission logic at all. It doesn't know it is being guarded — which is exactly the point.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Subject** | `EmployeeDao` | The shared interface, so the proxy is usable wherever the real object is |
| **Real Subject** | `EmployeeDaoImpl` | The actual business logic and data access |
| **Proxy** | `EmployeeDaoProxy` | Holds the real subject; controls access to it |
| **Client** | `EmployeeManagement` | Operates on the Subject, unaware which implementation it has |

## Lazy loading

The other everyday use — don't build the expensive thing until someone actually needs it:

```java title="Virtual proxy"
public class EmployeeDaoLazyProxy implements EmployeeDao {
    private EmployeeDao real;                     // not created yet

    private EmployeeDao real() {
        if (real == null) {
            real = new EmployeeDaoImpl();         // expensive - deferred to first use
        }
        return real;
    }

    @Override
    public void getEmployeeInfo(int empID) {
        real().getEmployeeInfo(empID);
    }
}
```

This is what Hibernate does when it returns an entity with uninitialised associations: you hold a proxy, and the query only fires when you first touch the field.

> **Tip** — Proxies are everywhere in frameworks, usually generated rather than written. Spring's `@Transactional`, `@Cacheable` and `@PreAuthorize` all work by wrapping your bean in a dynamically generated proxy that runs the cross-cutting behaviour and then delegates. It's also why calling an annotated method *from inside the same class* silently does nothing — that call bypasses the proxy.

## Proxy vs. Decorator

Structurally the same: implement an interface, hold one object of it, delegate.

| | Proxy | [Decorator](decorator.html) |
| --- | --- | --- |
| Intent | **Control access** to existing behaviour | **Add** new behaviour |
| Client awareness | Usually unaware | Deliberately assembles the chain |
| Stacking | Usually one layer | Commonly several |
| Lifecycle | Often creates/manages the real subject | Always receives an already-built object |

That last row is the practical tell: a decorator is **handed** its component; a proxy often **owns** it — as `EmployeeDaoProxy` does when it constructs `EmployeeDaoImpl` itself.

## Proxy vs. Facade

- **Proxy** wraps **one** object and keeps its interface.
- **[Facade](facade.html)** fronts **many** objects behind a new, simpler interface.

> **Warning** — Because the proxy is invisible to the client, it is also invisible when it misbehaves. A caching proxy serving stale data, or a lazy proxy firing a database query inside a loop, looks like the real object failing. If a proxy does anything non-trivial, make sure it logs.

> **Summary** — Proxy puts a substitutable stand-in in front of a real object so that access control, laziness, caching or instrumentation can be added without changing either the client or the real subject. It is the pattern behind most of what frameworks do to your objects on your behalf.
