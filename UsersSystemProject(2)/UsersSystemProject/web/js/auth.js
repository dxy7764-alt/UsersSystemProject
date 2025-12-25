async function auth() {
    //这里执行用户身份验证.....
    //这里执行用户身份验证.....
    let identify = window.outils.getCookie("currentUserName");
    let token = window.outils.getCookie("currentToken");
    console.log("in auth()=>identify=" + identify)
    console.log("in auth()=>token=" + token)
    if (identify && token) {
        let resp = await axios.get(baseUrl + "/users/auth", {
            withCredentials: true,
            params: {
                identify: identify,
                token: token
            }
        });
        console.log(resp);
        if (resp.data.code === 200) {
            console.log(resp.data);
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }

}