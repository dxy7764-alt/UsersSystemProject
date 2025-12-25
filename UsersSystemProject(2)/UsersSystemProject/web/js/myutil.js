//这个工具类就实现了把表单自动封装为一个json对象。
function conveterParamsToJson(paramsAndValues) {
    let jsonObj = {};

    let param = paramsAndValues.split("&");
    for (let i = 0; param != null && i < param.length; i++) {
        let para = param[i].split("=");
        if (jsonObj.hasOwnProperty(para[0])) {
            //console.log('该属性已经存在....')
            //console.log(para[0]);
            //console.log(jsonObj[para[0]])
            if (Array.isArray(jsonObj[para[0]])) {
                jsonObj[para[0]] = [...jsonObj[para[0]], para[1]]
            } else {
                jsonObj[para[0]] = [jsonObj[para[0]], para[1]]
            }
        } else {
            jsonObj[para[0]] = para[1];
        }

    }
    return jsonObj;
}

/**
 * 将表单数据封装为json
 * @param form
 * @returns
 */
function getFormData(form) {
    let formValues = $("#" + form).serialize()
    console.log(formValues)
    //关于jquery的serialize方法转换空格为+号的解决方法
    formValues = formValues.replace(/\+/g, " ");   // g表示对整个字符串中符合条件的都进行替换
    let temp = decodeURIComponent(JSON.stringify(conveterParamsToJson(formValues)));
    let queryParam = JSON.parse(temp);
    return queryParam;
}