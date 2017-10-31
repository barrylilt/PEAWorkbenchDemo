package com.saama.workbench.util;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Main {

	static List<Integer> sum = new ArrayList<>();;
	public static void main(String[] args) throws ParseException {
		Map map = new HashMap<String, String>();
		map.put("1", "Value1");
		map.put("2", "Value2");
		map.put("3", "Value3");
		System.out.println(map.values().stream().map(p -> "'" + PEAUtils.escapeSql(p.toString()) + "'").collect(Collectors.joining(", ")));
	}
	
	public static void sendMail() {
		String username = "nilesh.narkhede0@gmail.com";
		String password = "";

//		final String username = "your_user_name@gmail.com";
//        final String password = "yourpassword";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("nilesh.narkhede0@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("nilesh.narkhede@saama.com"));
            message.setSubject("Testing Subject");
            message.setText("Dear Mail Crawler,"
                + "\n\n No spam to my email, please!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
	}
	
	static void permute(int[] a, int k) {
		if (k == a.length) {
			int prevEl = -1, prevVal = 0, ySum = 0;
			for (int i = 0; i < a.length; i++) {
				if (prevEl < 0) {
					prevEl = a[i];
					ySum = 1;
					prevVal = 1;
				}
				else {
					if (prevEl < a[i]) {
						ySum += (prevVal + 1);
						prevVal = (prevVal + 1);
					}
					else {
						ySum++;
						prevVal = 1;
					}
				}
				System.out.print(" [" + a[i] + "] ");
			}
			System.out.println(" = " + sum);
			sum.add(ySum);
		} else {
			for (int i = k; i < a.length; i++) {
				int temp = a[k];
				a[k] = a[i];
				a[i] = temp;

				permute(a, k + 1);

				temp = a[k];
				a[k] = a[i];
				a[i] = temp;
			}
		}
	}
	
	public static void yAxis() {
		Scanner scanner = new Scanner(System.in);
		int cases = scanner.nextInt();
		
		while (cases -- > 0) {
			int elCnt = scanner.nextInt();
			int[] arr = new int[elCnt];
			while (elCnt > 0) {
				arr[elCnt-1] = scanner.nextInt();
				elCnt--;
			}
			permute(arr, 0);
		}
	}

	public static void print(int arr[]) {
		int currSum = arr[0];
		int maxSum = currSum;
		int ncSum = currSum;
		int currNc = currSum;

		for (int i = 1; i < arr.length; i++) {
			int val = arr[i];
			currSum = Math.max(val, currSum + val);
			currNc = Math.max(ncSum, ncSum + val);
			maxSum = Math.max(maxSum, currSum);
			ncSum = Math.max(maxSum, currNc);
		}

		System.out.println(maxSum + " " + ncSum);
	}

	private static void contNonContSums() {

		// Scanner scanner = new Scanner(System.in);
		// String str = scanner.next();
		//
		// for (int i = 0; i<str.length(); i++)

		// int cases = scanner.nextInt();
		//
		// while (cases-- > 0) {
		// int cnt = Integer.parseInt(scanner.next());
		// int[] arr = new int[cnt];
		// int i = 0;
		// while (i < cnt) {
		// int arrEl = Integer.parseInt(scanner.next());
		// arr[i++] = arrEl;
		// }
		// print(arr);
		// }
	}

	private static void repeatKsums() {

		Scanner scanner = new Scanner(System.in);
		int tc = Integer.parseInt(scanner.next());

		Map<Integer, Map<String, Object>> map = new HashMap<Integer, Map<String, Object>>();
		while (tc-- > 0) {
			System.out.println("inCnt - ");
			int inCnt = Integer.parseInt(scanner.next());
			System.out.println("incrementedCnt - ");
			int incrementedCnt = Integer.parseInt(scanner.next());
			List<Integer> list = new ArrayList<>();

			int totEl = inCnt * (inCnt + 1) / 2;

			System.out.println("totEl - ");
			while (totEl-- > 0) {
				list.add(Integer.parseInt(scanner.next()));
			}

			Map<String, Object> m = new HashMap<>();
			m.put("inCnt", inCnt);
			m.put("incrementedCnt", inCnt);
			m.put("el", list);

			ksumProc(inCnt, incrementedCnt, list);

			map.put(tc, m);

		}

	}

	private static void ksumProc(int inCnt, int incrementedCnt,
			List<Integer> list) {
		Stream<Integer> listSortedStream = list.stream().sorted();
		list = listSortedStream.collect(Collectors.toList());
		List<Integer> setOut = new ArrayList<Integer>();

		int first = list.get(0) / incrementedCnt;
		setOut.add(first);

		for (int x = 1; x < inCnt; x++) {
			setOut.add(list.get(x)
					- ((incrementedCnt - 1) == 0 ? 0
							: (first * (incrementedCnt - 1))));
		}
		System.out.print("Output - ");
		System.out.println(setOut.toString().replace(",", "") // remove the
																// commas
				.replace("[", "") // remove the right bracket
				.replace("]", "") // remove the left bracket
				.trim());
	}

	private static void streamAPI() {
		String[] str = new String[] { "Sachin", null, "Nilesh", "Sachin" };
		List<String> list = Arrays.asList(str).stream()
				.filter(string -> Objects.nonNull(string))
				// .sorted()
				.sorted(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						// return o1.compareTo(o2);
						if (o1.compareTo(o2) < -1) {
							return -1;
						} else if (o1.compareTo(o2) > 1) {
							return 1;
						}
						return 0;
					}
				}).collect(Collectors.toList());
		System.out.println(list);
	}

	private static void fibc() {
		Scanner scanner = new Scanner(System.in);
		String t1 = scanner.next();
		String t2 = scanner.next();
		long n = Long.parseLong(scanner.next());

		BigInteger Tn2 = new BigInteger(t1);
		BigInteger Tn1 = new BigInteger(t2);
		BigInteger Tn = new BigInteger("0");

		for (int i = 3; i <= n; i++) {
			Tn = Tn2.add(Tn1.multiply(Tn1));

			System.out.println(Tn2 + " - " + Tn1 + " - " + Tn);

			Tn2 = Tn1;
			Tn1 = Tn;
		}

		System.out.println(Tn);
	}

	private static void minString() {
		Scanner scanner = new Scanner(System.in);
		String loop = scanner.next();

		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

		for (int i = 0; i < Integer.parseInt(loop); i++) {
			String str = scanner.next();
			String oStr = new String();
			Integer[] intArr = new Integer[str.length()];
			for (int j = 0; j < str.length(); j++) {
				intArr[j] = characters.indexOf(str.charAt(j));
			}

			Arrays.sort(intArr, Collections.reverseOrder());

			for (int j = 0; j < intArr.length; j++) {
				oStr += characters.charAt(intArr[j]);
			}

			System.out.println(oStr);
		}

	}

	private static void matchString() {
		int matchLen = 3;
		List<String> alString = new ArrayList<String>();
		alString.add("STOP & SHOP C&S (MST)");
		alString.add("STOP & SHOP C&S");
		alString.add("STOP & SHOP (FRZ)");
		alString.add("TOPS TM DRY");
		alString.add("TOPS PERSONAL CARE");

		String mtchStr = "TOPS";

		for (String str : alString) {
			int len = mtchStr.length();
			int ptr1 = 0;
			int value = 0;
			for (int i = 0; i < len; i++) {
				String tokn = new String(mtchStr.substring(i,
						((i + matchLen) < len) ? i + matchLen : len));
				if (str.contains(tokn)) {
					value++;
				}
			}

			System.out.println(str + " - " + value);
		}
	}

	private static void calDistanceBtwnString(String str1, String str2) {
		String mtchStr = str1, otherStr = str2;
		int ptr1 = 0;
		int value = 0;

		if (str1.length() > str2.length()) {
			mtchStr = str2;
			otherStr = str1;
		}
		int len = mtchStr.length();

		for (int i = 2; i < len; i += 2) {
			String tokn = new String(mtchStr.substring(ptr1, i));
			if (otherStr.contains(tokn)) {
				value++;
			}
		}
	}

}
