package org.jhipster.health.web.rest;

import org.jhipster.health.HealthpointsApp;
import org.jhipster.health.domain.Preferences;
import org.jhipster.health.repository.PreferencesRepository;
import org.jhipster.health.repository.UserRepository;
import org.jhipster.health.repository.search.PreferencesSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.jhipster.health.domain.enumeration.Units;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test class for the PreferencesResource REST controller.
 *
 * @see PreferencesResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HealthpointsApp.class)
@WebAppConfiguration
@IntegrationTest
public class PreferencesResourceIntTest {


    private static final Integer DEFAULT_WEEKLY_GOAL = 10;
    private static final Integer UPDATED_WEEKLY_GOAL = 11;

    private static final Units DEFAULT_WEIGHT_UNITS = Units.kg;
    private static final Units UPDATED_WEIGHT_UNITS = Units.lb;

    @Inject
    private PreferencesRepository preferencesRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PreferencesSearchRepository preferencesSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPreferencesMockMvc;

    private Preferences preferences;

    @Autowired
    private WebApplicationContext context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PreferencesResource preferencesResource = new PreferencesResource();
        ReflectionTestUtils.setField(preferencesResource, "preferencesSearchRepository", preferencesSearchRepository);
        ReflectionTestUtils.setField(preferencesResource, "preferencesRepository", preferencesRepository);
        ReflectionTestUtils.setField(preferencesResource, "userRepository", userRepository);
        this.restPreferencesMockMvc = MockMvcBuilders.standaloneSetup(preferencesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        preferencesSearchRepository.deleteAll();
        preferences = new Preferences();
        preferences.setWeekly_goal(DEFAULT_WEEKLY_GOAL);
        preferences.setWeight_units(DEFAULT_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void createPreferences() throws Exception {
        int databaseSizeBeforeCreate = preferencesRepository.findAll().size();

        // create security-aware mockMvc
        restPreferencesMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // Create the Preferences

        restPreferencesMockMvc.perform(post("/api/preferences")
                .with(user("user"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(preferences)))
                .andExpect(status().isCreated());

        // Validate the Preferences in the database
        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeCreate + 1);
        Preferences testPreferences = preferences.get(preferences.size() - 1);
        assertThat(testPreferences.getWeekly_goal()).isEqualTo(DEFAULT_WEEKLY_GOAL);
        assertThat(testPreferences.getWeight_units()).isEqualTo(DEFAULT_WEIGHT_UNITS);

        // Validate the Preferences in ElasticSearch
        Preferences preferencesEs = preferencesSearchRepository.findOne(testPreferences.getId());
        assertThat(preferencesEs).isEqualToComparingFieldByField(testPreferences);
    }

    @Test
    @Transactional
    public void checkWeekly_goalIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferencesRepository.findAll().size();
        // set the field null
        preferences.setWeekly_goal(null);

        // Create the Preferences, which fails.

        restPreferencesMockMvc.perform(post("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(preferences)))
                .andExpect(status().isBadRequest());

        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWeight_unitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferencesRepository.findAll().size();
        // set the field null
        preferences.setWeight_units(null);

        // Create the Preferences, which fails.

        restPreferencesMockMvc.perform(post("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(preferences)))
                .andExpect(status().isBadRequest());

        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferences
        restPreferencesMockMvc.perform(get("/api/preferences?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(preferences.getId().intValue())))
                .andExpect(jsonPath("$.[*].weekly_goal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
                .andExpect(jsonPath("$.[*].weight_units").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }

    @Test
    @Transactional
    public void getPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get the preferences
        restPreferencesMockMvc.perform(get("/api/preferences/{id}", preferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(preferences.getId().intValue()))
            .andExpect(jsonPath("$.weekly_goal").value(DEFAULT_WEEKLY_GOAL))
            .andExpect(jsonPath("$.weight_units").value(DEFAULT_WEIGHT_UNITS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPreferences() throws Exception {
        // Get the preferences
        restPreferencesMockMvc.perform(get("/api/preferences/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);
        preferencesSearchRepository.save(preferences);
        int databaseSizeBeforeUpdate = preferencesRepository.findAll().size();

        // Update the preferences
        Preferences updatedPreferences = new Preferences();
        updatedPreferences.setId(preferences.getId());
        updatedPreferences.setWeekly_goal(UPDATED_WEEKLY_GOAL);
        updatedPreferences.setWeight_units(UPDATED_WEIGHT_UNITS);

        restPreferencesMockMvc.perform(put("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPreferences)))
                .andExpect(status().isOk());

        // Validate the Preferences in the database
        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeUpdate);
        Preferences testPreferences = preferences.get(preferences.size() - 1);
        assertThat(testPreferences.getWeekly_goal()).isEqualTo(UPDATED_WEEKLY_GOAL);
        assertThat(testPreferences.getWeight_units()).isEqualTo(UPDATED_WEIGHT_UNITS);

        // Validate the Preferences in ElasticSearch
        Preferences preferencesEs = preferencesSearchRepository.findOne(testPreferences.getId());
        assertThat(preferencesEs).isEqualToComparingFieldByField(testPreferences);
    }

    @Test
    @Transactional
    public void deletePreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);
        preferencesSearchRepository.save(preferences);
        int databaseSizeBeforeDelete = preferencesRepository.findAll().size();

        // Get the preferences
        restPreferencesMockMvc.perform(delete("/api/preferences/{id}", preferences.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean preferencesExistsInEs = preferencesSearchRepository.exists(preferences.getId());
        assertThat(preferencesExistsInEs).isFalse();

        // Validate the database is empty
        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);
        preferencesSearchRepository.save(preferences);

        // Search the preferences
        restPreferencesMockMvc.perform(get("/api/_search/preferences?query=id:" + preferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(preferences.getId().intValue())))
            .andExpect(jsonPath("$.[*].weekly_goal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
            .andExpect(jsonPath("$.[*].weight_units").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }
}
