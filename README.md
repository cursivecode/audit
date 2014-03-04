# audit

A Clojure library for verify map data

## Installation

Add the following dependency to your `project.clj` file:

```
  [audit "0.1.0"]
```

## Usage

The ```audit``` function takes two maps, the ```audit-map``` and the ```value-map```

If the two maps are don't have the same keys, the audit function will throw an exception.

The value map will be normal data

```clojure
  (def value-map {:url "http://www.somepage.com"
                         :title "the number 1"
                         :time 400})
```

The audit map is a map consisting of keyword and vectors of functions.
Each function will be ran on the input of the matching ```value-map``` keyword.

```clojure
(def audit-map {:url [string? (regex #"http://www.")]
                       :title [string? (regex #"\d")]
                       :time [number? #(> % 300)]}
```

The audit map does not reject empty collection, so the function must handle that possibility.

Calling the audit function

```clojure
(audit audit-map value-map)
```

Will return a map, with two important keys ```valid-audit``` and ```failure-reason```

```valid-audit``` will be true if audit was valid and false otherwise

if ```valid-audit``` is false, ```failure-reason``` will return a string saying what key and input caused the failure.


## License

Copyright © 2014 Michael Doaty

Distributed under the Eclipse Public License, same as Clojure.     