define(['app'], function (app) {
    var srvUrl = "/rcm-rest";
    app
        .constant('BEWG_URL', {
            // 登录
            LoginUrl: srvUrl + "login/login",

            // 数据字典
            SaveOrUpdateDict : srvUrl + "dict/saveOrUpdateDictType.do",

            // 用户管理
            SelectAllUser: srvUrl + "user/getAll.do",
            SelectUserById: srvUrl + "user/getSysUserByID.do",
            SelectAllRoleUser : srvUrl + "user/getAllRole.do",
            SelectUserRole : srvUrl + "user/getRoleByUserId.do",
            SaveOrUpdateUserRole : srvUrl + "user/saveUserRole.do",
            SaveUser: "fnd/SysUser/createSysUser",
            UpdateUser: "fnd/SysUser/updateSysUser",

            // 菜单管理
            SelectAllSysFun: "fnd/SysFunction/getAll",
            SelectSysFunById: "fnd/SysFunction/getSysFunctionByID",
            SelectAllOrgSysFun: "fnd/SysFunction/getOrg",
            UpdateSysFunStateById: "fnd/SysFunction/updateSysFunctionState",
            SelectSysFunCode: "fnd/SysFunction/SelectCode",
            DeleteSysFunById: "fnd/SysFunction/delectSysFunctionByID",
            SaveSysFun: "fnd/SysFunction/createSysFunction",
            UpdateSysFun: "fnd/SysFunction/updateSysFunction",
            ValidateSysFunForm : "fnd/SysFunction/Validate",
        });
});