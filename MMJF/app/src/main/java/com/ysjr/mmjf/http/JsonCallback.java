package com.ysjr.mmjf.http;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.entity.VoidHttpResult;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.popup.LoginAgainDialog;
import com.ysjr.mmjf.utils.GsonConvert;
import com.ysjr.mmjf.utils.HttpError;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-11-16.
 *
 * Type的5种类型
 * Class:代表的就是Class对象,没有泛型的对象
 * ParameterizedType：这就是上面我们代码中用到的，他代表的是一个泛型类型，比如Point<T>，它就是一个泛型类型。
 * TypeVariable：这个代表的就是泛型变量，例如Point,这里面的T就是泛型变量，而如果我们利用一种方法获得的对象是T,那它对应的类型就是TypeVariable；（这个类型的应用后面会细讲）
 * WildcardType：上面的TypeVariable对应的是泛型变量，而如果我们得到不是泛型变量，而是通配符比如：? extends
 * Integer,那它对应的类型就是WildcardType；
 * GenericArrayType：如果我们得到的是类似String[]这种数组形式的表达式，那它对应的类型就是GenericArrayType，非常值得注意的是如果type对应的是表达式是ArrayList这种的，这个type类型应该是ParameterizedType，而不是GenericArrayType，只有类似Integer[]这种的才是GenericArrayType类型。
 * 虽然我们后面会对TypeVariable，WildcardType进行讲解，这里还是先对他们三个类型对应的意义先总结一下，比如我们这里的clazz.getGenericSuperclass()，得到的Type对应的是完整的泛型表达式即：Point，那它对应的类型就是ParameterizedType，如果我们得到的Type对应的表达式，仅仅是Point中用来填充泛型变量T的Integer,那这个Type对应的类型就是TypeVariable，如果我们得到的是依然是填充泛型变量T的填充类型，这而个填充类型却是通配符？，那这个Type对应的类型就是WildcardType。这一段看不大明白也没关系，后面还会再讲。
 */

public abstract class JsonCallback<T> extends AbsCallback<T> {

  @Override public T convertResponse(Response response) throws Throwable {
    Request request = response.request();
    String result = response.body().string();
    if (TextUtils.isEmpty(result)) throw new IllegalStateException();
    JSONObject jsonObject = new JSONObject(result);
    int status = jsonObject.getInt("status");
    String msg = jsonObject.getString("msg");
    //        toke失效重新获取token,获取token成功后再重新获取数据
    if (status != 200) {
      if (status == 4001) {
        boolean syncToken = BaseActivity.isRefreshTokenSuccess();
        if (syncToken) {
          response = OkGo.getInstance().getOkHttpClient().newCall(request).execute();
          result = response.body().string();
        }
      } else if (status == 4004) {
        ActivityUtils.finishToActivity(MainActivity.class, false);
        final Activity activity = ActivityUtils.getTopActivity();
        //解决弹窗弹两次的问题
        if (activity instanceof MainActivity) {
          if (((MainActivity) activity).isPause) {
            return null;
          }
        }
        activity.runOnUiThread(new Runnable() {
          @Override public void run() {
            boolean isCustomer = false;
            User user = DataSupport.findFirst(User.class);
            if (user != null && user.type == 1) {
              isCustomer = true;
            }
            LoginAgainDialog dialog = new LoginAgainDialog(activity, isCustomer);
            dialog.show();
          }
        });
      } else {
        throw new IllegalStateException(String.valueOf(status),new Throwable(msg));
      }
    }

    //解析父类泛型
    Type type = getClass().getGenericSuperclass();//获取泛型父类type对象
    if (type == null) throw new IllegalStateException();
    if (type instanceof ParameterizedType) {//解析type,获取泛型父类和填充类型
      Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();//填充类型
      Type argument = typeArguments[0];//只有一个泛型参数T
      if (argument instanceof ParameterizedType) {
        return parseParameterizedType(result, (ParameterizedType) argument);
      } else if (argument instanceof Class) {
        return parseClass(result, (Class) argument);
      }
    }
    return null;
  }

  private T parseClass(String result, Class clazz) throws Throwable {
    if (clazz == null) throw new IllegalStateException();
    if (TextUtils.isEmpty(result)) throw new IllegalStateException();
    return (T) new JSONObject(result);
  }

  private T parseParameterizedType(String result, ParameterizedType type) throws Throwable {
    if (type == null) throw new IllegalStateException();
    if (TextUtils.isEmpty(result)) throw new IllegalStateException();
    Type rawType = type.getRawType();
    Type argumentType = type.getActualTypeArguments()[0];
    if (rawType != HttpResult.class) {
      //            不是标准返回数据
      throw new IllegalStateException();
    } else {
      if (argumentType == Void.class) {
        VoidHttpResult voidHttpResult = GsonConvert.fromJson(result, VoidHttpResult.class);
        return (T) voidHttpResult.toHttpResult();
      }
      HttpResult httpResult = GsonConvert.fromJson(result, type);
      return (T) httpResult;
    }
  }

  @Override public void onError(com.lzy.okgo.model.Response<T> response) {
    super.onError(response);
    Throwable exception = response.getException();
    Throwable cause = exception.getCause();
    if (exception == null) {
      return;
    }
    String errStatus = exception.getMessage();
    if (TextUtils.isEmpty(errStatus)) {
      return;
    }
    if (!HttpError.EMPTY.equals(errStatus)) {
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort(cause.getMessage());
    }
  }
}
