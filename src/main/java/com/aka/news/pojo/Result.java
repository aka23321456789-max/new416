package com.aka.news.pojo;
import lombok.Data;


@Data
public class Result {
    private String code;//状态码
    private String msg;//提示信息
    private Object data;//
    public static Result success(Object data) {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("成功");
        result.setData(data);
        return result;
    }
    public static Result success() {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("成功");
        return result;
    }

    public static Result fail(String msg) {
       Result result = new Result();
       result.setCode("500");
       result.setMsg(msg);
       result.setData("操作错误");
       return result;
    }
}
