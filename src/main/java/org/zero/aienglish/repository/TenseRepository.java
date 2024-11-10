package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.TenseEntity;
import org.zero.aienglish.model.Tense;

import java.util.List;

public interface TenseRepository extends JpaRepository<TenseEntity, Integer> {
    @Query(value = """
	select
	    te.id,\s
        te.formula,\s
        te.verb,\s
        concat(ti.title, ' ', du.title) as tense
    from tense te, time ti, duration du\s
    where ti.id = te.title_time and du.id = te.title_duration\s
        and concat(ti.title, ' ', du.title) ilike any(array[?1]);
""", nativeQuery = true)
    List<Tense> getTenseListByTitle(String[] title);

    @Query(value = """
select
	t.id,
	concat(ti.title, ' ', d.title) as tense,
	t.formula,
	t.verb
from tense t, time ti, duration d
where ti.id = t.title_time and d.id = t.title_duration and t.id != 2
order by random()
limit 3;
""", nativeQuery = true)
    List<Tense> getRandomTenseList(Integer ignore);
}
