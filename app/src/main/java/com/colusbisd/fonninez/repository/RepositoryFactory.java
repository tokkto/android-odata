package com.colusbisd.fonninez.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colusbisd.fonninez.service.SAPServiceManager;

import com.sap.cloud.android.odata.foninez.foninez;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntitySets;

import com.sap.cloud.android.odata.foninez.AcuerdoType;
import com.sap.cloud.android.odata.foninez.AsistenciaType;
import com.sap.cloud.android.odata.foninez.BusinessPartnerType;
import com.sap.cloud.android.odata.foninez.MediadorType;
import com.sap.cloud.android.odata.foninez.MotivoType;
import com.sap.cloud.android.odata.foninez.OperacionType;
import com.sap.cloud.android.odata.foninez.ProgramaType;
import com.sap.cloud.android.odata.foninez.SesionProgramadaType;

import com.sap.cloud.mobile.odata.EntitySet;
import com.sap.cloud.mobile.odata.Property;

import java.util.WeakHashMap;

/*
 * Repository factory to construct repository for an entity set
 */
public class RepositoryFactory {

    /*
     * Cache all repositories created to avoid reconstruction and keeping the entities of entity set
     * maintained by each repository in memory. Use a weak hash map to allow recovery in low memory
     * conditions
     */
    private WeakHashMap<String, Repository> repositories;

    /*
     * Service manager to interact with OData service
     */
    private SAPServiceManager sapServiceManager;

    /**
     * Construct a RepositoryFactory instance. There should only be one repository factory and used
     * throughout the life of the application to avoid caching entities multiple times.
     * @param sapServiceManager - Service manager for interaction with OData service
     */
    public RepositoryFactory(SAPServiceManager sapServiceManager) {
        repositories = new WeakHashMap<>();
        this.sapServiceManager = sapServiceManager;
    }

    /**
     * Construct or return an existing repository for the specified entity set
     * @param entitySet - entity set for which the repository is to be returned
     * @param orderByProperty - if specified, collection will be sorted ascending with this property
     * @return a repository for the entity set
     */
    public Repository getRepository(@NonNull EntitySet entitySet, @Nullable Property orderByProperty) {
        foninez foninez = sapServiceManager.getfoninez();
        String key = entitySet.getLocalName();
        Repository repository = repositories.get(key);
        if (repository == null) {
            if (key.equals(EntitySets.acuerdo.getLocalName())) {
                repository = new Repository<AcuerdoType>(foninez, EntitySets.acuerdo, orderByProperty);
            } else if (key.equals(EntitySets.asistencia.getLocalName())) {
                repository = new Repository<AsistenciaType>(foninez, EntitySets.asistencia, orderByProperty);
            } else if (key.equals(EntitySets.businessPartner.getLocalName())) {
                repository = new Repository<BusinessPartnerType>(foninez, EntitySets.businessPartner, orderByProperty);
            } else if (key.equals(EntitySets.mediador.getLocalName())) {
                repository = new Repository<MediadorType>(foninez, EntitySets.mediador, orderByProperty);
            } else if (key.equals(EntitySets.motivo.getLocalName())) {
                repository = new Repository<MotivoType>(foninez, EntitySets.motivo, orderByProperty);
            } else if (key.equals(EntitySets.operacion.getLocalName())) {
                repository = new Repository<OperacionType>(foninez, EntitySets.operacion, orderByProperty);
            } else if (key.equals(EntitySets.programa.getLocalName())) {
                repository = new Repository<ProgramaType>(foninez, EntitySets.programa, orderByProperty);
            } else if (key.equals(EntitySets.sesionProgramada.getLocalName())) {
                repository = new Repository<SesionProgramadaType>(foninez, EntitySets.sesionProgramada, orderByProperty);
            } else {
                throw new AssertionError("Fatal error, entity set[" + key + "] missing in generated code");
            }
            repositories.put(key, repository);
        }
        return repository;
    }

    /**
     * Get rid of all cached repositories
     */
    public void reset() {
        repositories.clear();
    }
 }
