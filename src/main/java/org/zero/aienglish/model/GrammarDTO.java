package org.zero.aienglish.model;

public interface GrammarDTO {
    Integer getId();
    String getDefaultWord();
    Integer getVocabulary_id();
    String getFirstForm();
    String getSecondForm();
    String getThirdForm();
    Boolean getIsModal();
}
