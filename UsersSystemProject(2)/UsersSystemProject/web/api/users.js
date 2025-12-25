// 若axios未全局引入，需手动导入
// import axios from 'axios';

// 若outils不存在，可补充简易的Cookie获取方法
if (!window.outils) {
    window.outils = {
        getCookie: function(name) {
            let arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
            if (arr != null) return unescape(arr[2]);
            return null;
        },
        // 补充setCookie和removeCookie方法（避免登录页调用时报错）
        setCookie: function(name, value, minutes) {
            let exp = new Date();
            exp.setTime(exp.getTime() + minutes * 60 * 1000);
            document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";path=/";
        },
        removeCookie: function(name) {
            this.setCookie(name, "", -1);
        }
    };
}

// api/users.js
// 前端用户业务逻辑接口（修复版）
// 全局函数形式，优化Token携带逻辑

// 1. 用户登录【核心修复：登录接口不携带Token请求头】
function login(loginUser, token, code) {
    return axios.post(baseUrl + '/users/login', loginUser, {
        withCredentials: true,
        params: {
            token: token,
            code: code
        }
        // 【已删除】登录接口不需要携带Token（此时还未获取有效Token）
        // headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 2. 用户注册【修复：兼容空Token，未登录时传空字符串】
function reg(regUser) {
    return axios.post(baseUrl + '/users/reg', regUser, {
        // 未登录时Token为null，传空字符串避免后端校验失败
        headers: { token: window.outils.getCookie("currentToken") || "" }
    }).then(res => res.data);
}

// 3. 查询所有用户
function queryAll() {
    return axios.get(baseUrl + '/users/queryAll', {
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 4. 新增用户接口（复用注册接口）【修复：兼容空Token】
function addUser(user) {
    return axios.post(baseUrl + '/users/reg', user, {
        headers: { token: window.outils.getCookie("currentToken") || "" }
    }).then(res => res.data);
}

// 5. 根据用户编号查询用户的资料
function queryUserByUid(uid) {
    return axios.get(baseUrl + '/users/queryById/' + uid, {
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 6. 更新用户接口
function updateUser(user) {
    return axios.put(baseUrl + '/users/update', user, {
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 7. 删除用户接口
function deleteUserByUid(uid) {
    return axios.delete(baseUrl + '/users/delete/' + uid, {
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 8. 检查用户名是否被注册【修复：兼容空Token】
async function checkUsernameIsReg(username) {
    let flag = true;
    let resp = await axios.get(baseUrl + '/users/queryByUsername', {
        params: {
            username: username
        },
        // 未登录时Token为null，传空字符串避免后端校验失败
        headers: { token: window.outils.getCookie("currentToken") || "" }
    });

    if (resp.data.code === 200) {
        if (resp.data.data.length > 0) {
            console.log("用户名存在。。。。")
            flag = false;
        }
    }

    console.log("in checkUsernameIsReg()=>" + flag);
    return flag;
}

// 9. 分页查询用户
function queryUserByPage(currentPageNum) {
    return axios.get(baseUrl + '/users/getUsersPager', {
        withCredentials: true,
        params: { currentPageNum: currentPageNum },
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 10. 批量删除用户
function deleteBatch(uids) {
    return axios.delete(baseUrl + '/users/deleteBatchUsers', {
        data: uids,
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

// 11. 按班级查询用户
function queryByBanji(banji) {
    return axios.get(baseUrl + '/users/queryByBanji', {
        withCredentials: true,
        params: { banji: banji },
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}