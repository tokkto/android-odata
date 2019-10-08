package com.colusbisd.fonninez.mdui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.colusbisd.fonninez.app.SAPWizardApplication;
import com.colusbisd.fonninez.app.UsageUtil;
import com.colusbisd.fonninez.mdui.acuerdo.AcuerdoActivity;
import com.colusbisd.fonninez.mdui.asistencia.AsistenciaActivity;
import com.colusbisd.fonninez.mdui.businesspartner.BusinessPartnerActivity;
import com.colusbisd.fonninez.mdui.mediador.MediadorActivity;
import com.colusbisd.fonninez.mdui.motivo.MotivoActivity;
import com.colusbisd.fonninez.mdui.operacion.OperacionActivity;
import com.colusbisd.fonninez.mdui.programa.ProgramaActivity;
import com.colusbisd.fonninez.mdui.sesionprogramada.SesionProgramadaActivity;
import com.sap.cloud.mobile.fiori.object.ObjectCell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.colusbisd.fonninez.R;


/*
 * An activity to display the list of all entity types from the OData service
 */
public class EntitySetListActivity extends AppCompatActivity {

    private static final int SETTINGS_SCREEN_ITEM = 200;
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySetListActivity.class);
    private static final int BLUE_ANDROID_ICON = R.drawable.ic_android_blue;
    private static final int WHITE_ANDROID_ICON = R.drawable.ic_android_white;

    public enum EntitySetName {
        Acuerdo("Acuerdo", R.string.eset_acuerdo,BLUE_ANDROID_ICON),
        Asistencia("Asistencia", R.string.eset_asistencia,WHITE_ANDROID_ICON),
        BusinessPartner("BusinessPartner", R.string.eset_businesspartner,BLUE_ANDROID_ICON),
        Mediador("Mediador", R.string.eset_mediador,WHITE_ANDROID_ICON),
        Motivo("Motivo", R.string.eset_motivo,BLUE_ANDROID_ICON),
        Operacion("Operacion", R.string.eset_operacion,WHITE_ANDROID_ICON),
        Programa("Programa", R.string.eset_programa,BLUE_ANDROID_ICON),
        SesionProgramada("SesionProgramada", R.string.eset_sesionprogramada,WHITE_ANDROID_ICON);

        private int titleId;
        private int iconId;
        private String entitySetName;

        EntitySetName(String name, int titleId, int iconId) {
            this.entitySetName = name;
            this.titleId = titleId;
            this.iconId = iconId;
        }

        public int getTitleId() {
                return this.titleId;
        }

        public String getEntitySetName() {
                return this.entitySetName;
        }
    }

    private final List<String> entitySetNames = new ArrayList<>();
    private final Map<String, EntitySetName> entitySetNameMap = new HashMap<>();
    private UsageUtil usageUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entity_set_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usageUtil = ((SAPWizardApplication) getApplication()).getUsageUtil();
        usageUtil.eventBehaviorViewDisplayed(EntitySetListActivity.class.getSimpleName(),
                "elementId", "onCreate", "called");

        entitySetNames.clear();
        entitySetNameMap.clear();
        for (EntitySetName entitySet : EntitySetName.values()) {
            String entitySetTitle = getResources().getString(entitySet.getTitleId());
            entitySetNames.add(entitySetTitle);
            entitySetNameMap.put(entitySetTitle, entitySet);
        }

        final ListView listView = findViewById(R.id.entity_list);
        final EntitySetListAdapter adapter = new EntitySetListAdapter(this, R.layout.element_entity_set_list, entitySetNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            EntitySetName entitySetName = entitySetNameMap.get(adapter.getItem(position));
            usageUtil.eventBehaviorUserInteraction(EntitySetListActivity.class.getSimpleName(),
                    "position: " + position, "onClicked", entitySetName.getEntitySetName());
            Context context = EntitySetListActivity.this;
            Intent intent;
            switch (entitySetName) {
                case Acuerdo:
                    intent = new Intent(context, AcuerdoActivity.class);
                    break;
                case Asistencia:
                    intent = new Intent(context, AsistenciaActivity.class);
                    break;
                case BusinessPartner:
                    intent = new Intent(context, BusinessPartnerActivity.class);
                    break;
                case Mediador:
                    intent = new Intent(context, MediadorActivity.class);
                    break;
                case Motivo:
                    intent = new Intent(context, MotivoActivity.class);
                    break;
                case Operacion:
                    intent = new Intent(context, OperacionActivity.class);
                    break;
                case Programa:
                    intent = new Intent(context, ProgramaActivity.class);
                    break;
                case SesionProgramada:
                    intent = new Intent(context, SesionProgramadaActivity.class);
                    break;
                    default:
                        return;
            }
            context.startActivity(intent);
        });
            
    }

    public class EntitySetListAdapter extends ArrayAdapter<String> {

        EntitySetListAdapter(@NonNull Context context, int resource, List<String> entitySetNames) {
            super(context, resource, entitySetNames);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            EntitySetName entitySetName = entitySetNameMap.get(getItem(position));
            if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_entity_set_list, parent, false);
            }
            String headLineName = getResources().getString(entitySetName.titleId);
            ObjectCell entitySetCell = convertView.findViewById(R.id.entity_set_name);
            entitySetCell.setHeadline(headLineName);
            entitySetCell.setDetailImage(entitySetName.iconId);
            return convertView;
        }
    }
                
    @Override
    public void onBackPressed() {
            moveTaskToBack(true);
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SETTINGS_SCREEN_ITEM, 0, R.string.menu_item_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGGER.debug("onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case SETTINGS_SCREEN_ITEM:
                LOGGER.debug("settings screen menu item selected.");
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivityForResult(intent, SETTINGS_SCREEN_ITEM);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGGER.debug("EntitySetListActivity::onActivityResult, request code: " + requestCode + " result code: " + resultCode);
        if (requestCode == SETTINGS_SCREEN_ITEM) {
            LOGGER.debug("Calling AppState to retrieve settings after settings screen is closed.");
        }
    }

}
