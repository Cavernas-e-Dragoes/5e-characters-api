package ced.characters.management.vo;

import ced.characters.management.models.Equipment;
import ced.characters.management.models.Magic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CharacterSheetDTO {

    private Long id;

    private String raceName;

    private String className;

    private String login;

    private String name;

    private Long race;


    private Long charClass;


    private List<Equipment> equipments;


    private List<Magic> magics;


    private Integer level = 1;


    private String Alignment;


    private Integer HitPoints;


    private Integer experiencePoints = 0;


    private Integer initiative;


    private Float speed;


    private Integer armorClass;


    private Integer inspiration;


    private Integer passiveWisdom;


    private Float height;


    private Float weight;


    private Integer age;


    /**
     * Attributes
     */


    private Integer strength;


    private Integer dexterity;


    private Integer constitution;


    private Integer intelligence;


    private Integer wisdom;


    private Integer charisma;

    /**
     * Saving Throws
     */


    private Boolean STStrength;


    private Boolean STDexterity;


    private Boolean STConstitution;


    private Boolean STIntelligence;


    private Boolean STWisdom;


    private Boolean STCharisma;

    /**
     * Skills
     */


    private Boolean acrobatics;


    private Boolean animalHandling;


    private Boolean arcana;


    private Boolean athletics;


    private Boolean deception;


    private Boolean history;


    private Boolean insight;


    private Boolean intimidation;


    private Boolean investigation;


    private Boolean medicine;


    private Boolean nature;


    private Boolean perception;


    private Boolean performance;


    private Boolean persuasion;


    private Boolean religion;


    private Boolean sleightOfHand;


    private Boolean stealth;


    private Boolean survival;


}