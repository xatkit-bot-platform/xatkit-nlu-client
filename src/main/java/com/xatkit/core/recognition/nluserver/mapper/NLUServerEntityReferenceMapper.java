package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.EntityMapper;
import com.xatkit.intent.CustomEntityDefinition;
import com.xatkit.intent.EntityDefinition;

import static com.xatkit.intent.EntityType.*;

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
        this.addEntityMapping(DATE_TIME, "@sys.date-time");
        this.addEntityMapping(DATE, "@sys.date");
        this.addEntityMapping(DATE_PERIOD, "@sys.date-period");
        this.addEntityMapping(TIME, "@sys.time");
        this.addEntityMapping(TIME_PERIOD, "@sys.time-period");
    }

    /**
     * Registers Number-related entities.
     */
    private void registerNumberEntities() {
        this.addEntityMapping(NUMBER, "@sys.number");
        this.addEntityMapping(CARDINAL, "@sys.cardinal");
        this.addEntityMapping(ORDINAL, "@sys.ordinal");
        this.addEntityMapping(INTEGER, "@sys.number-integer");
        this.addEntityMapping(NUMBER_SEQUENCE, "@sys.number-sequence");
        this.addEntityMapping(FLIGHT_NUMBER, "@sys.flight-number");
    }

    /**
     * Registers Amount-related entities.
     */

    private void registerAmountEntities() {
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
    }

    /**
     * Registers Geography-related entities.
     */
    private void registerGeographyEntities() {
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
    }

    /**
     * Registers Contact-related entities.
     */
    private void registerContactEntities() {
        this.addEntityMapping(EMAIL, "@sys.email");
        this.addEntityMapping(PHONE_NUMBER, "@sys.phone-number");
    }

    /**
     * Registers Names-related entities.
     */
    private void registerNamesEntities() {
        this.addEntityMapping(GIVEN_NAME, "@sys.given-name");
        this.addEntityMapping(LAST_NAME, "@sys.last-name");
    }

    /**
     * Registers other entities.
     */
    private void registerOtherEntities() {
        this.addEntityMapping(COLOR, "@sys.color");
        this.addEntityMapping(LANGUAGE, "@sys.language");
    }

    /**
     * Registers generic entities.
     */
    private void registerGenericEntities() {
        this.addEntityMapping(ANY, "@sys.any");
        this.addEntityMapping(URL, "@sys.url");
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
