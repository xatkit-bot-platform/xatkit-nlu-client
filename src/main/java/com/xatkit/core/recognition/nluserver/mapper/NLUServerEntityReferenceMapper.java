package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.EntityMapper;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.intent.CustomEntityDefinition;
import com.xatkit.intent.EntityDefinition;

import java.text.MessageFormat;

import static com.xatkit.intent.EntityType.ADDRESS;
import static com.xatkit.intent.EntityType.AGE;
import static com.xatkit.intent.EntityType.AIRPORT;
import static com.xatkit.intent.EntityType.ANY;
import static com.xatkit.intent.EntityType.CAPITAL;
import static com.xatkit.intent.EntityType.CARDINAL;
import static com.xatkit.intent.EntityType.CITY;
import static com.xatkit.intent.EntityType.CITY_GB;
import static com.xatkit.intent.EntityType.CITY_US;
import static com.xatkit.intent.EntityType.COLOR;
import static com.xatkit.intent.EntityType.COUNTRY;
import static com.xatkit.intent.EntityType.COUNTRY_CODE;
import static com.xatkit.intent.EntityType.COUNTY_GB;
import static com.xatkit.intent.EntityType.COUNTY_US;
import static com.xatkit.intent.EntityType.DATE;
import static com.xatkit.intent.EntityType.DATE_PERIOD;
import static com.xatkit.intent.EntityType.DATE_TIME;
import static com.xatkit.intent.EntityType.DURATION;
import static com.xatkit.intent.EntityType.EMAIL;
import static com.xatkit.intent.EntityType.FLIGHT_NUMBER;
import static com.xatkit.intent.EntityType.GIVEN_NAME;
import static com.xatkit.intent.EntityType.INTEGER;
import static com.xatkit.intent.EntityType.LANGUAGE;
import static com.xatkit.intent.EntityType.LAST_NAME;
import static com.xatkit.intent.EntityType.LOCATION;
import static com.xatkit.intent.EntityType.MUSIC_ARTIST;
import static com.xatkit.intent.EntityType.MUSIC_GENRE;
import static com.xatkit.intent.EntityType.NUMBER;
import static com.xatkit.intent.EntityType.NUMBER_SEQUENCE;
import static com.xatkit.intent.EntityType.ORDINAL;
import static com.xatkit.intent.EntityType.PERCENTAGE;
import static com.xatkit.intent.EntityType.PHONE_NUMBER;
import static com.xatkit.intent.EntityType.PLACE_ATTRACTION;
import static com.xatkit.intent.EntityType.PLACE_ATTRACTION_GB;
import static com.xatkit.intent.EntityType.PLACE_ATTRACTION_US;
import static com.xatkit.intent.EntityType.STATE;
import static com.xatkit.intent.EntityType.STATE_GB;
import static com.xatkit.intent.EntityType.STATE_US;
import static com.xatkit.intent.EntityType.STREET_ADDRESS;
import static com.xatkit.intent.EntityType.TEMPERATURE;
import static com.xatkit.intent.EntityType.TIME;
import static com.xatkit.intent.EntityType.TIME_PERIOD;
import static com.xatkit.intent.EntityType.UNIT_AREA;
import static com.xatkit.intent.EntityType.UNIT_CURRENCY;
import static com.xatkit.intent.EntityType.UNIT_INFORMATION;
import static com.xatkit.intent.EntityType.UNIT_LENGTH;
import static com.xatkit.intent.EntityType.UNIT_SPEED;
import static com.xatkit.intent.EntityType.UNIT_VOLUME;
import static com.xatkit.intent.EntityType.UNIT_WEIGHT;
import static com.xatkit.intent.EntityType.URL;
import static com.xatkit.intent.EntityType.ZIP_CODE;

/**
 * An {@link EntityMapper} initialized with DialogFlow's system entities.
 * <p>
 * This class provides a mapping of {@link com.xatkit.intent.EntityType}s to Xatkit's NLU Server system entities. Mapped
 * entities can be accessed by calling {@link #getMappingFor(EntityDefinition)}.
 */
public class NLUServerEntityReferenceMapper extends EntityMapper {

    /**
     * Constructs a {@link NLUServerEntityReferenceMapper} initialized with DialogFlow's system entities.
     */
    public NLUServerEntityReferenceMapper() {
        super();
        this.registerDateTimeEntities();
        this.registerNumberEntities();
        this.registerAmountEntities();
        this.registerGeographyEntities();
        this.registerContactEntities();
        this.registerNamesEntities();
        this.registerOtherEntities();
        this.registerGenericEntities();
        this.setFallbackEntityMapping("@sys.any");
    }

    /**
     * Registers Date and Time-related entities.
     */
    private void registerDateTimeEntities() {
      /* TO IMPLEMENT
        this.addEntityMapping(DATE_TIME, "@sys.date-time");
        this.addEntityMapping(DATE, "@sys.date");
        this.addEntityMapping(DATE_PERIOD, "@sys.date-period");
        this.addEntityMapping(TIME, "@sys.time");
        this.addEntityMapping(TIME_PERIOD, "@sys.time-period");

       */
    }

    /**
     * Registers Number-related entities.
     */
    private void registerNumberEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(NUMBER, "@sys.number");
        this.addEntityMapping(CARDINAL, "@sys.cardinal");
        this.addEntityMapping(ORDINAL, "@sys.ordinal");
        this.addEntityMapping(INTEGER, "@sys.number-integer");
        this.addEntityMapping(NUMBER_SEQUENCE, "@sys.number-sequence");
        this.addEntityMapping(FLIGHT_NUMBER, "@sys.flight-number");

         */
    }

    /**
     * Registers Amount-related entities.
     */

    private void registerAmountEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(UNIT_AREA, "@sys.unit-area");
        this.addEntityMapping(UNIT_CURRENCY, "@sys.unit-currency");
        this.addEntityMapping(UNIT_LENGTH, "@sys.unit-length");
        this.addEntityMapping(UNIT_SPEED, "@sys.unit-speed");
        this.addEntityMapping(UNIT_VOLUME, "@sys.unit-volume");
        this.addEntityMapping(UNIT_WEIGHT, "@sys.unit-weight");
        this.addEntityMapping(UNIT_INFORMATION, "@sys.unit-information");
        this.addEntityMapping(PERCENTAGE, "@sys.percentage");
        this.addEntityMapping(TEMPERATURE, "@sys.temperature");
        this.addEntityMapping(DURATION, "@sys.duration");
        this.addEntityMapping(AGE, "@sys.age");

         */
    }

    /**
     * Registers Geography-related entities.
     */
    private void registerGeographyEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(ADDRESS, "@sys.address");
        this.addEntityMapping(STREET_ADDRESS, "@sys.street-address");
        this.addEntityMapping(ZIP_CODE, "@sys.zip-code");
        this.addEntityMapping(CAPITAL, "@sys.geo-capital");
        this.addEntityMapping(COUNTRY, "@sys.geo-country");
        this.addEntityMapping(COUNTRY_CODE, "@sys.geo-country-code");
        this.addEntityMapping(CITY, "@sys.geo-city");
        this.addEntityMapping(STATE, "@sys.geo-state");
        this.addEntityMapping(CITY_US, "@sys.geo-city-us");
        this.addEntityMapping(STATE_US, "@sys.geo-state-us");
        this.addEntityMapping(COUNTY_US, "@sys.geo-county-us");
        this.addEntityMapping(CITY_GB, "@sys.geo-city-gb");
        this.addEntityMapping(STATE_GB, "@sys.geo-state-gb");
        this.addEntityMapping(COUNTY_GB, "@sys.geo-county-gb");
        this.addEntityMapping(PLACE_ATTRACTION_US, "@sys.place-attraction-us");
        this.addEntityMapping(PLACE_ATTRACTION_GB, "@sys.place-attraction-gb");
        this.addEntityMapping(PLACE_ATTRACTION, "@sys.place-attraction");
        this.addEntityMapping(AIRPORT, "@sys.airport");
        this.addEntityMapping(LOCATION, "@sys.location");

         */
    }

    /**
     * Registers Contact-related entities.
     */
    private void registerContactEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(EMAIL, "@sys.email");
        this.addEntityMapping(PHONE_NUMBER, "@sys.phone-number");

         */
    }

    /**
     * Registers Names-related entities.
     */
    private void registerNamesEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(GIVEN_NAME, "@sys.given-name");
        this.addEntityMapping(LAST_NAME, "@sys.last-name");

         */
    }

    /**
     * Registers other entities.
     */
    private void registerOtherEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(COLOR, "@sys.color");
        this.addEntityMapping(LANGUAGE, "@sys.language");

         */
    }

    /**
     * Registers generic entities.
     */
    private void registerGenericEntities() {
        /* TO IMPLEMENT
        this.addEntityMapping(ANY, "@sys.any");
        this.addEntityMapping(URL, "@sys.url");

         */
    }


    /**
     * Maps the provided {@code customEntityDefinition} to its NLUServer implementation.
     * <p>
     * Entities are registered independently from the intents. This two-step process allows to reference
     * entities with their names, and do not require to store any mapping information in the
     * {@link NLUServerEntityReferenceMapper}.
     *
     * @param customEntityDefinition the {@link CustomEntityDefinition} to retrieve the concrete entity
     *                               {@link String} from
     * @return a {@link String} identifying the NLUServer entity corresponding to the provided {@code
     * customEntityDefinition, right now }
     */
    @Override
    protected String getMappingForCustomEntity(CustomEntityDefinition customEntityDefinition) {
        //return "@" + customEntityDefinition.getName();
        return customEntityDefinition.getName();
    }
}
