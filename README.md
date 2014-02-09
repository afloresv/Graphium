GraphDB-Benchmark
=================

##Observations

Missing filter in Q07: `FILTER (?date > 2008Y06M20D )`

Missing filter in Q08: `#FILTER langMatches( lang(?text), "EN" )`

Missing filter in Q10: `FILTER (?date > 2008Y06M20D )`

Order by in Q10 `#ORDER BY xsd:double(str(?price))` not working as it should; Ordering by string ?price.
