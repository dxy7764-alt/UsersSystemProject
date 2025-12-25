// api/users.js（修改后）
// 去掉所有export，直接定义全局函数
function login(loginUser, token, code) {
    return axios.post(baseUrl + '/users/login', loginUser, {
        withCredentials: true,
        params: { token: token, code: code },
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function reg(regUser) {
    return axios.post(baseUrl + '/users/reg', regUser, {
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function queryAll() {
    return axios.get(baseUrl + '/users/queryAll', {
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function addUser(user) {
    return axios.post(baseUrl + '/users/reg', user, {
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function queryUserByUid(uid) {
    return axios.get(baseUrl + '/users/queryById/' + uid, {
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function updateUser(user) {
    return axios.put(baseUrl + '/users/update', user, {
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function deleteUserByUid(uid) {
    return axios.delete(baseUrl + '/users/delete/' + uid, {
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

async function checkUsernameIsReg(username) {
    let flag = true;
    let resp = await axios.get(baseUrl + '/users/queryByUsername', {
        params: { username: username },
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    });
    if (resp.data.code === 200 && resp.data.data.length > 0) {
        flag = false;
    }
    return flag;
}

function queryUserByPage(currentPageNum) {
    return axios.get(baseUrl + '/users/getUsersPager', {
        withCredentials: true,
        params: { currentPageNum: currentPageNum },
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}

function deleteBatch(uids){
    return axios.delete(baseUrl+'/users/deleteBatchUsers',{
        data: uids,
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res=>res.data);
}

// 新增的按班级查询函数（改为全局函数）
function queryByBanji(banji) {
    return axios.get(baseUrl + '/users/queryByBanji', {
        withCredentials: true,
        params: { banji: banji },
        // ========== 新增：携带Token到请求头 ==========
        headers: { token: window.outils.getCookie("currentToken") }
    }).then(res => res.data);
}