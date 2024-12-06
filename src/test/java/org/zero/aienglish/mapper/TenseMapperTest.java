package org.zero.aienglish.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.entity.Duration;
import org.zero.aienglish.entity.Tense;
import org.zero.aienglish.entity.TimeEntity;
import org.zero.aienglish.model.TenseDTO;

class TenseMapperTest {
    private TenseMapper tenseMapper;
    private TenseDTO tenseDTO;
    private org.zero.aienglish.model.Tense tense;
    private Tense tenseEntity;

    @BeforeEach
    void setUp() {
        tenseMapper = new TenseMapperImpl();

        var duration = new Duration();
        duration.setId(1);
        duration.setTitle("Simple");

        var time = new TimeEntity();
        time.setId(1);
        time.setTitle("Present");

        tenseEntity = new Tense();
        tenseEntity.setId(1);
        tenseEntity.setVerb("test");
        tenseEntity.setFormula("test + test");
        tenseEntity.setTitleDuration(duration);
        tenseEntity.setTitleTime(time);
        tenseDTO = new TenseDTO(1, "Present Simple", "test + test", "test");

        tense = new org.zero.aienglish.model.Tense() {
            @Override
            public Integer getId() {
                return 1;
            }

            @Override
            public String getFormula() {
                return tenseEntity.getFormula();
            }

            @Override
            public String getVerb() {
                return tenseEntity.getVerb();
            }

            @Override
            public String getTense() {
                return tenseDTO.tense();
            }
        };

    }

    @Test
    @DisplayName("Tense into TenseDTO Mapper")
    void map() {
        var res = tenseMapper.map(tenseEntity);

        Assertions.assertEquals(res.formula(), tenseDTO.formula());
        Assertions.assertEquals(res.tense(), tenseDTO.tense());
        Assertions.assertEquals(res.verb(), tenseDTO.verb());
    }

    @Test
    void map_2() {
        var res = tenseMapper.map(tense);

        System.out.println(res.id());
        System.out.println(res.tense());
        Assertions.assertEquals(tenseDTO.id(), res.id());
        Assertions.assertEquals(tenseDTO.formula(), res.formula());
        Assertions.assertEquals(tenseDTO.tense(), res.tense());
        Assertions.assertEquals(tenseDTO.verb(), res.verb());
    }

    @Test
    void map_3() {
        var res = tenseMapper.map(tenseDTO);

        System.out.println(res.getId());
        System.out.println(res.getWord());
        Assertions.assertEquals(tenseDTO.tense(), res.getWord());
        Assertions.assertEquals(tenseDTO.id(), res.getId());
        Assertions.assertEquals(false, res.getIsMarker());
        Assertions.assertEquals("__", res.getTranslate());
        Assertions.assertNull(res.getSpeechPart());
    }
}