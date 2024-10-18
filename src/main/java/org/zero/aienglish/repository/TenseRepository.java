package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Tense;
import org.zero.aienglish.model.TenseDTO;

import java.util.List;

public interface TenseRepository extends JpaRepository<Tense, Integer> {
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
    List<TenseDTO> getTenseListByTitle(String[] title);

}
