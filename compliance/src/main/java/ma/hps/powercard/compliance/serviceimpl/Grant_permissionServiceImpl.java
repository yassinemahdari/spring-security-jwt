package ma.hps.powercard.compliance.serviceimpl;

import org.fornax.cartridges.sculptor.framework.accessapi.ConditionalCriteria;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContextStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ma.hps.exception.OurException;
import ma.hps.powercard.compliance.domain.Component;
import ma.hps.powercard.compliance.domain.ComponentProperties;
import ma.hps.powercard.compliance.domain.Grant_permission;
import ma.hps.powercard.compliance.domain.Grant_permissionProperties;
import ma.hps.powercard.compliance.domain.Menu;
import ma.hps.powercard.compliance.domain.MenuProperties;
import ma.hps.powercard.compliance.domain.Role;
import ma.hps.powercard.compliance.domain.RoleProperties;
import ma.hps.powercard.compliance.domain.Screen;
import ma.hps.powercard.compliance.domain.ScreenProperties;
import ma.hps.powercard.compliance.domain.Type_permission;
import ma.hps.powercard.compliance.domain.Type_permissionProperties;
import ma.hps.powercard.compliance.exception.Grant_permissionNotFoundException;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filterVO;
import ma.hps.powercard.compliance.serviceapi.Data_columns_filter_valuesVO;
import ma.hps.powercard.compliance.serviceapi.Grant_permissionVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of Grant_permissionService.
 */
@Lazy
@Service("grant_permissionService")
public class Grant_permissionServiceImpl extends Grant_permissionServiceImplBase {
    public Grant_permissionServiceImpl() {
    }

    /**
    * Persist a Grant_permission entity .
    *
    * @param Grant_permissionVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0024 If ever the object already exists.
    *
    */
    public String createGrant_permissionService(ServiceContext ctx,
        Grant_permissionVO grant_permissionVO) throws Exception {
        ServiceContextStore.set(ctx);

        Grant_permission grant_permission = new Grant_permission();

        if (grant_permissionVO.getType_permission_fk() != null) {
            grant_permission.setPermission_id(new Type_permission(
                    grant_permissionVO.getType_permission_fk()));

        }

        if (grant_permissionVO.getMenu_fk() != null) {
            grant_permission.setMenu_id(new Menu(
                    grant_permissionVO.getMenu_fk()));

        }

        if (grant_permissionVO.getScreen_fk() != null) {
            grant_permission.setScreen_id(new Screen(
                    grant_permissionVO.getScreen_fk()));

        }

        if (grant_permissionVO.getComponent_fk() != null) {
            grant_permission.setComponent_id(new Component(
                    grant_permissionVO.getComponent_fk()));

        }

        if (grant_permissionVO.getRole_fk() != null) {
            grant_permission.setRole_id(new Role(
                    grant_permissionVO.getRole_fk()));

        }

        grant_permission.setType_grant(grant_permissionVO.getType_grant());

        Grant_permission grant_permission1 =
            this.getGrant_permissionRepository().save(grant_permission);

        return "0000";
    }

    /**
    * update a Grant_permission entity .
    *
    * @param Grant_permissionVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String updateGrant_permissionService(ServiceContext ctx,
        Grant_permissionVO grant_permissionVO) throws Exception {
        ServiceContextStore.set(ctx);

        Grant_permission grant_permission = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Grant_permissionProperties.grant_permission_id(),
                grant_permissionVO.getGrant_permission_id()));

        List<Grant_permission> list =
            this.getGrant_permissionRepository().findByCondition(con);

        if (list.size() > 0) {
            grant_permission = list.get(0);
        } else {
            throw new OurException("0001",
                new Grant_permissionNotFoundException(""));
        }

        if (grant_permissionVO.getType_permission_fk() != null) {
            grant_permission.setPermission_id(new Type_permission(
                    grant_permissionVO.getType_permission_fk()));

        }

        if (grant_permissionVO.getMenu_fk() != null) {
            grant_permission.setMenu_id(new Menu(
                    grant_permissionVO.getMenu_fk()));

        }

        if (grant_permissionVO.getScreen_fk() != null) {
            grant_permission.setScreen_id(new Screen(
                    grant_permissionVO.getScreen_fk()));

        }

        if (grant_permissionVO.getComponent_fk() != null) {
            grant_permission.setComponent_id(new Component(
                    grant_permissionVO.getComponent_fk()));

        }

        if (grant_permissionVO.getRole_fk() != null) {
            grant_permission.setRole_id(new Role(
                    grant_permissionVO.getRole_fk()));

        }

        if (grant_permissionVO.getType_grant() != null) {
            grant_permission.setType_grant(grant_permissionVO.getType_grant());
        }

        Grant_permission grant_permission1 =
            this.getGrant_permissionRepository().save(grant_permission);

        return "0000";
    }

    /**
    * delete a Grant_permission entity .
    *
    * @param Grant_permissionVO ValueObject.
    *
    * @return 0000 - if Success
    *
    * @throws  OurException No.0001 If the object don't exists.
    *
    */
    public String deleteGrant_permissionService(ServiceContext ctx,
        Grant_permissionVO grant_permissionVO) throws Exception {
        ServiceContextStore.set(ctx);

        Grant_permission grant_permission = null;

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        con.add(ConditionalCriteria.equal(
                Grant_permissionProperties.grant_permission_id(),
                grant_permissionVO.getGrant_permission_id()));

        List<Grant_permission> list =
            this.getGrant_permissionRepository().findByCondition(con);

        if (list.size() > 0) {
            grant_permission = list.get(0);
        } else {
            throw new OurException("0001",
                new Grant_permissionNotFoundException(""));
        }

        if (grant_permissionVO.getType_permission_fk() != null) {
            grant_permission.setPermission_id(new Type_permission(
                    grant_permissionVO.getType_permission_fk()));

        }

        if (grant_permissionVO.getMenu_fk() != null) {
            grant_permission.setMenu_id(new Menu(
                    grant_permissionVO.getMenu_fk()));

        }

        if (grant_permissionVO.getScreen_fk() != null) {
            grant_permission.setScreen_id(new Screen(
                    grant_permissionVO.getScreen_fk()));

        }

        if (grant_permissionVO.getComponent_fk() != null) {
            grant_permission.setComponent_id(new Component(
                    grant_permissionVO.getComponent_fk()));

        }

        if (grant_permissionVO.getRole_fk() != null) {
            grant_permission.setRole_id(new Role(
                    grant_permissionVO.getRole_fk()));

        }

        this.getGrant_permissionRepository().delete(grant_permission);

        return "0000";
    }

    /**
    *  Find all entities of a specific type .
    *
    * @return List of Grant_permissionVO
    *
    */
    public List<Grant_permissionVO> getAllGrant_permissionService(
        ServiceContext ctx) throws Exception {
        List<Grant_permissionVO> l = new ArrayList<Grant_permissionVO>();

        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            dataFilter(ctx, con);
        }

        List<Grant_permission> l_entity = new ArrayList<Grant_permission>();

        l_entity = this.getGrant_permissionRepository().findByCondition(con);

        for (int i = 0; i < l_entity.size(); i++) {
            l.add(entityToVO(ctx, l_entity.get(i), 0, 0));

        }

        return l;

    }

    /**
    * Find entities by conditions
    *
     * @return List of Grant_permissionVO
    *
    */
    public List<Grant_permissionVO> searchGrant_permissionService(
        ServiceContext ctx, Grant_permissionVO grant_permissionVO)
        throws Exception {
        List<ConditionalCriteria> con = new ArrayList<ConditionalCriteria>();

        if (ctx != null) {
            dataFilter(ctx, con);
        }

        if (grant_permissionVO.getGrant_permission_id() != null) {
            con.add(ConditionalCriteria.equal(
                    Grant_permissionProperties.grant_permission_id(),
                    grant_permissionVO.getGrant_permission_id()));
        }

        if (grant_permissionVO.getType_grant() != null) {
            con.add(ConditionalCriteria.equal(
                    Grant_permissionProperties.type_grant(),
                    grant_permissionVO.getType_grant()));
        }

        if (grant_permissionVO.getScreen_code() != null &&
              !grant_permissionVO.getScreen_code().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Grant_permissionProperties.screen_id()
                                                                       .screen_code(),
                    grant_permissionVO.getScreen_code()));
        }

        if (grant_permissionVO.getScreen_refered() != null &&
              !grant_permissionVO.getScreen_refered().equals("")) {
            con.add(ConditionalCriteria.likeOrEqual(Grant_permissionProperties.component_id()
                                                                       .screen_parent()
                                                                       .screen_code(),
                    grant_permissionVO.getScreen_refered()));
        }

        if (grant_permissionVO.getType_permission_fk() != null) {
            con.add(ConditionalCriteria.equal(Grant_permissionProperties.permission_id()
                                                                        .permission_id(),
                    grant_permissionVO.getType_permission_fk()));
        }

        if (grant_permissionVO.getMenu_fk() != null) {
            con.add(ConditionalCriteria.equal(Grant_permissionProperties.menu_id()
                                                                        .menu_id(),
                    grant_permissionVO.getMenu_fk()));
        }

        if (grant_permissionVO.getScreen_fk() != null) {
            con.add(ConditionalCriteria.equal(Grant_permissionProperties.screen_id()
                                                                        .screen_id(),
                    grant_permissionVO.getScreen_fk()));
        }

        if (grant_permissionVO.getComponent_fk() != null) {
            con.add(ConditionalCriteria.equal(Grant_permissionProperties.component_id()
                                                                        .component_id(),
                    grant_permissionVO.getComponent_fk()));
        }

        if (grant_permissionVO.getRole_fk() != null) {
            con.add(ConditionalCriteria.equal(Grant_permissionProperties.role_id()
                                                                        .role_id(),
                    grant_permissionVO.getRole_fk()));
        }
        
		if (grant_permissionVO.isConditionIn()) {
            con.add(ConditionalCriteria.in(Grant_permissionProperties.role_id()
                                                                     .role_id(),
                    grant_permissionVO.getColRoles()));

        }

        con.add(ConditionalCriteria.orderAsc(
                Grant_permissionProperties.type_grant()));

        List<Grant_permission> l_entity = new ArrayList<Grant_permission>();
        List<Grant_permissionVO> l = new ArrayList<Grant_permissionVO>();

        l_entity = this.getGrant_permissionRepository().findByCondition(con);

        for (int i = 0; i < l_entity.size(); i++) {
            Grant_permissionVO grant_permissionVoTmp =
                entityToVO(ctx, l_entity.get(i), 0, 0);

            l.add(grant_permissionVoTmp);

        }

        return l;

    }

    /**
    * Convert an entity to ValueObject.
    *
    * @param Grant_permission Entity.
    *
    * @return Grant_permissionVO ValueObject.
    *
    */
    public Grant_permissionVO entityToVO(ServiceContext ctx,
        Grant_permission grant_permission, int lazy_level, int lazy_level_col)
        throws Exception {
        Grant_permissionVO e = new Grant_permissionVO();

        e.setGrant_permission_id(grant_permission.getGrant_permission_id());

        e.setType_grant(grant_permission.getType_grant());

        if (grant_permission.getPermission_id() == null) {
            e.setType_permission_fk(null);
        }
        else {
            e.setType_permission_fk(grant_permission.getPermission_id()
                                                    .getPermission_id());
        }

        if (grant_permission.getMenu_id() == null) {
            e.setMenu_fk(null);
        }
        else {
            e.setMenu_fk(grant_permission.getMenu_id().getMenu_id());
        }

        if (grant_permission.getScreen_id() == null) {
            e.setScreen_fk(null);
        }
        else {
            e.setScreen_fk(grant_permission.getScreen_id().getScreen_id());
        }

        if (grant_permission.getComponent_id() == null) {
            e.setComponent_fk(null);
        }
        else {
            e.setComponent_fk(grant_permission.getComponent_id()
                                              .getComponent_id());
        }

        if (grant_permission.getRole_id() == null) {
            e.setRole_fk(null);
        }
        else {
            e.setRole_fk(grant_permission.getRole_id().getRole_id());
        }

        return e;
    }

    public Grant_permission VoToEntity(ServiceContext ctx,
        Grant_permissionVO grant_permissionVO) throws Exception {
        Grant_permission grant_permission;

		if (grant_permissionVO.getGrant_permission_id() == null)
				grant_permission = new Grant_permission();
		else
                grant_permission = new Grant_permission(grant_permissionVO.getGrant_permission_id());

        if (grant_permissionVO.getType_permission_fk() != null) {
            grant_permission.setPermission_id(new Type_permission(
                    grant_permissionVO.getType_permission_fk()));

        }

        if (grant_permissionVO.getMenu_fk() != null) {
            grant_permission.setMenu_id(new Menu(
                    grant_permissionVO.getMenu_fk()));

        }

        if (grant_permissionVO.getScreen_fk() != null) {
            grant_permission.setScreen_id(new Screen(
                    grant_permissionVO.getScreen_fk()));

        }

        if (grant_permissionVO.getComponent_fk() != null) {
            grant_permission.setComponent_id(new Component(
                    grant_permissionVO.getComponent_fk()));

        }

        if (grant_permissionVO.getRole_fk() != null) {
            grant_permission.setRole_id(new Role(
                    grant_permissionVO.getRole_fk()));

        }

        grant_permission.setType_grant(grant_permissionVO.getType_grant());

        return grant_permission;
    }

    private void dataFilter(ServiceContext ctx, List<ConditionalCriteria> con)
        throws Exception {
        for (Data_columns_filterVO vo : ctx.getDataColumnsFilterList()) {
            String entity_name =
                vo.getRef_filter().getRef_pwc_tables().getEntity_name();
            String column_name =
                vo.getRef_columns_filter().getRef_pwc_columns().getColumn_name();
            String column_type =
                vo.getRef_columns_filter().getRef_pwc_columns().getColumn_type();
            String naturalKey =
                vo.getRef_columns_filter().getRef_pwc_columns().getNaturalKey();
            String apply_filter =
                vo.getRef_filter().getRef_pwc_tables().getApply_filter();
            String operation =
                vo.getRef_columns_filter().getOperation().toUpperCase();

            Data_columns_filter_valuesVO data_columns_filter_valuesVO =
                new Data_columns_filter_valuesVO();

            data_columns_filter_valuesVO.setData_columns_filter_fk(vo.getData_columns_filter_id());

            Collection<Data_columns_filter_valuesVO> col =
                vo.getData_columns_filter_values_col();
            Iterator<Data_columns_filter_valuesVO> iter = col.iterator();

            List<Object> listVals = new ArrayList<Object>();

            while (iter.hasNext()) {
                if (column_type.equals("Long")) {
                    listVals.add(Long.valueOf(iter.next().getVal()));
                } else {
                    listVals.add(iter.next().getVal());
                }
            }

            if (listVals.size() > 0) {
                if (entity_name.equals(Grant_permission.class.getName()) &&
                      apply_filter.equals("Y")) {
                    Grant_permissionProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(
                                Grant_permissionProperties.dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Grant_permissionProperties.dynamicAttribute(),
                                    listVals)));
                    }
                }

                if (entity_name.equals(Type_permission.class.getName()) &&
                      apply_filter.equals("Y")) {
                    Type_permissionProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(Grant_permissionProperties.permission_id()
                                                                                 .dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Grant_permissionProperties.permission_id()
                                                              .dynamicAttribute(),
                                    listVals)));
                    }
                }

                if (entity_name.equals(Menu.class.getName()) &&
                      apply_filter.equals("Y")) {
                    MenuProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(Grant_permissionProperties.menu_id()
                                                                                 .dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Grant_permissionProperties.menu_id()
                                                              .dynamicAttribute(),
                                    listVals)));
                    }
                }

                if (entity_name.equals(Screen.class.getName()) &&
                      apply_filter.equals("Y")) {
                    ScreenProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(Grant_permissionProperties.screen_id()
                                                                                 .dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Grant_permissionProperties.screen_id()
                                                              .dynamicAttribute(),
                                    listVals)));
                    }
                }

                if (entity_name.equals(Component.class.getName()) &&
                      apply_filter.equals("Y")) {
                    ComponentProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(Grant_permissionProperties.component_id()
                                                                                 .dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Grant_permissionProperties.component_id()
                                                              .dynamicAttribute(),
                                    listVals)));
                    }
                }

                if (entity_name.equals(Role.class.getName()) &&
                      apply_filter.equals("Y")) {
                    RoleProperties.dynamicAttribute = column_name.toLowerCase();

                    if (operation.equals("I")) {
                        con.add(ConditionalCriteria.in(Grant_permissionProperties.role_id()
                                                                                 .dynamicAttribute(),
                                listVals));
                    } else {
                        con.add(ConditionalCriteria.not(ConditionalCriteria.in(
                                    Grant_permissionProperties.role_id()
                                                              .dynamicAttribute(),
                                    listVals)));
                    }
                }
            }
        }
    }
}
