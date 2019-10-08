package com.colusbisd.fonninez.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.os.Parcelable;

import com.colusbisd.fonninez.viewmodel.acuerdotype.AcuerdoTypeViewModel;
import com.colusbisd.fonninez.viewmodel.asistenciatype.AsistenciaTypeViewModel;
import com.colusbisd.fonninez.viewmodel.businesspartnertype.BusinessPartnerTypeViewModel;
import com.colusbisd.fonninez.viewmodel.mediadortype.MediadorTypeViewModel;
import com.colusbisd.fonninez.viewmodel.motivotype.MotivoTypeViewModel;
import com.colusbisd.fonninez.viewmodel.operaciontype.OperacionTypeViewModel;
import com.colusbisd.fonninez.viewmodel.programatype.ProgramaTypeViewModel;
import com.colusbisd.fonninez.viewmodel.sesionprogramadatype.SesionProgramadaTypeViewModel;


/**
 * Custom factory class, which can create view models for entity subsets, which are
 * reached from a parent entity through a navigation property.
 */
public class EntityViewModelFactory implements ViewModelProvider.Factory {

	// application class
    private Application application;
	// name of the navigation property
    private String navigationPropertyName;
	// parent entity
    private Parcelable entityData;

	/**
	 * Creates a factory class for entity view models created following a navigation link.
	 *
	 * @param application parent application
	 * @param navigationPropertyName name of the navigation link
	 * @param entityData parent entity
	 */
    public EntityViewModelFactory(Application application, String navigationPropertyName, Parcelable entityData) {
        this.application = application;
        this.navigationPropertyName = navigationPropertyName;
        this.entityData = entityData;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        T retValue = null;
		switch(modelClass.getSimpleName()) {



			case "AcuerdoTypeViewModel":
				retValue = (T) new AcuerdoTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "AsistenciaTypeViewModel":
				retValue = (T) new AsistenciaTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "BusinessPartnerTypeViewModel":
				retValue = (T) new BusinessPartnerTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "MediadorTypeViewModel":
				retValue = (T) new MediadorTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "MotivoTypeViewModel":
				retValue = (T) new MotivoTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "OperacionTypeViewModel":
				retValue = (T) new OperacionTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "ProgramaTypeViewModel":
				retValue = (T) new ProgramaTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "SesionProgramadaTypeViewModel":
				retValue = (T) new SesionProgramadaTypeViewModel(application, navigationPropertyName, entityData);
				break;
		}
		return retValue;
	}
}