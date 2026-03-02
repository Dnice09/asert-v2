package tz.go.mnrt.asert.helpers;

import static org.apache.commons.beanutils.ConvertUtils.convert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import tz.go.mnrt.asert.modules.menugroup.entity.MenuGroup;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;

public class Utils {

  public static String[] ones = {
    "",
    "one",
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
    "ten",
    "eleven",
    "twelve",
    "thirteen",
    "fourteen",
    "fifteen",
    "sixteen",
    "seventeen",
    "eighteen",
    "nineteen"
  };

  public static String[] tens = {
    "", // 0
    "", // 1
    "twenty", // 2
    "thirty", // 3
    "forty", // 4
    "fifty", // 5
    "sixty", // 6
    "seventy", // 7
    "eighty", // 8
    "ninety" // 9
  };

  public static String convertNumberToWord(double money) {
    long dollars = (long) money;
    long cents = Math.round((money - dollars) * 100);
    if (money == 0D) {
      return "";
    }
    if (money < 0) {
      return " Invalid";
    }
    String dollarsPart = "";
    if (dollars > 0) {
      dollarsPart = convert(dollars) + " dollar" + (dollars == 1 ? "" : "s");
    }
    String centsPart = "";
    if (cents > 0) {
      if (dollarsPart.length() > 0) {
        centsPart = " and ";
      }
      centsPart += convert(cents) + " cent" + (cents == 1 ? "" : "s");
    }
    return dollarsPart + centsPart;
  }

  public static String capitalizeWords(String givenString) {
    String[] arr = givenString.split(" ");
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < arr.length; i++) {
      sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
    }
    return sb.toString().trim();
  }

  public static Map<String, List<Long>> getMenuGroup(Set<MenuItem> menuItems) {

    Map<String, List<Long>> groupItemIds = new HashMap<>();
    List<Long> menuGroupIds = new ArrayList<>();
    List<Long> menuItemsIds = new ArrayList<>();
    menuGroupIds.add(0L);
    menuItemsIds.add(0L);

    menuItems.forEach(
        m -> {
          menuItemsIds.add(m.getId());
          if (m.getMenuGroup() != null) {
            getGroup(m.getMenuGroup(), menuGroupIds);
          }
        });
    groupItemIds.put("groupIds", menuGroupIds);
    groupItemIds.put("itemIds", menuItemsIds);
    return groupItemIds;
  }

  private static void getGroup(MenuGroup group, List<Long> collect) {
    collect.add(group.getId());
    //        if (group.getParentMenuGroup() != null) {
    //            getGroup(group.getParentMenuGroup(), collect);
    //        }
  }

  public static <T, Z> T safeNew(Z entity, Function<Z, T> func) {
    if (entity == null) return null;
    return func.apply(entity);
  }

  public static String createReservationCode(Integer hotelId,String roomNumber){
      LocalDate date = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
      String formattedDate = date.format(formatter);
      return formattedDate+"-"+hotelId+"-"+roomNumber;
  }
}
