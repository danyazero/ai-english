package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Tense;

import java.util.List;

public interface TenseRepository extends JpaRepository<Tense, Integer> {
    @Query(value = """
	select
	    te.id,
        te.formula,
        te.verb,
        concat(ti.title, ' ', du.title) as tense
    from tense te, time ti, duration du
    where ti.id = te.time_id and du.id = te.duration_id
    and concat(ti.title, ' ', du.title) ilike any(array[?1]);
""", nativeQuery = true)
    List<org.zero.aienglish.model.Tense> getTenseListByTitle(String[] title);

    @Query(value = """
select
	t.id,
	concat(ti.title, ' ', d.title) as tense,
	t.formula,
	t.verb
from tense t, time ti, duration d
where ti.id = t.time_id and d.id = t.duration_id and t.id != 2
order by random()
limit 3;
""", nativeQuery = true)
    List<org.zero.aienglish.model.Tense> getRandomTenseList(Integer ignore);

}
