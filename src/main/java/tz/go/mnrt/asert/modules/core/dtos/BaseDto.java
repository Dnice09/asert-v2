package tz.go.mnrt.asert.modules.core.dtos;

import java.util.function.Function;

public class BaseDto {
  public static <T extends BaseDto, Z> T safeNew(Z entity, Function<Z, T> func) {
    if (entity == null) return null;
    return func.apply(entity);
  }
}
