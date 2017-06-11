package com.mx.otac.scan.data.dummy;

import java.util.Arrays;
import java.util.Collection;

import com.mx.otac.scan.domain.DashboardNotification;

public abstract class DummyDataGenerator {

    static String randomFirstName() {
        String[] names = { "Daniel", "Miguel", "Karla", "Juan", "Luis",
                "Bernardo", "Ana", "Miriam", "Elisa", "Jorge", "Matias", "Pedro",
                "Alfredo", "Katia", "Alfonso", "Patricia", "Samantha", "Samuel", "Linda",
                "Jeremias", "Sara", "Raymundo", "Maria", "Susana" };
        return names[(int) (Math.random() * names.length)];
    }

    static String randomLastName() {
        String[] names = { "Hern�ndez", "Garc�a", "Mercado", "Ram�rez",
                "Rodr�guez", "Gonz�lez", "Mart�nez", "L�pez", "Valero", "Quintero",
                "P�rez", "S�nchez", "Flores", "Mart�nez", "D�az", "Cruz",
                "Castro", "Camarena", "Sanchez", "M�rquez", "Barroso", "Pe�a",
                "Camacho", "�vila", "Corona", "Velarde", "Vera", "Sandoval",
                "Sol�s", "Garcia", "Andrade", "Acosta", "Lopez", "Castillo",
                "Ramos", "M�ndez", "Bichir", "Almada", "Berm�dez", "Rivera",
                "Salinas", "Calderon", "De Le�n", "Quivera", "Duarte", "Diaz" };
        return names[(int) (Math.random() * names.length)];
    }

    static String randomCompanyName() {

        String name = randomName();
        if (Math.random() < 0.03) {
            name += " Technologies";
        } else if (Math.random() < 0.02) {
            name += " Investment";
        }
        if (Math.random() < 0.3) {
            name += " Inc";
        } else if (Math.random() < 0.2) {
            name += " Ltd.";
        }

        return name;
    }

    public static String randomWord(int len, boolean capitalized) {
        String[] part = { "ger", "ma", "isa", "app", "le", "ni", "ke", "mic",
                "ro", "soft", "wa", "re", "lo", "gi", "is", "acc", "el", "tes",
                "la", "ko", "ni", "ka", "so", "ny", "mi", "nol", "ta", "pa",
                "na", "so", "nic", "sa", "les", "for", "ce" };
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            String p = part[(int) (Math.random() * part.length)];
            if (i == 0 && capitalized) {
                p = Character.toUpperCase(p.charAt(0)) + p.substring(1);
            }
            sb.append(p);
        }
        return sb.toString();

    }

    public static String randomText(int words) {
        StringBuffer sb = new StringBuffer();
        int sentenceWordsLeft = 0;
        while (words-- > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            if (sentenceWordsLeft == 0 && words > 0) {
                sentenceWordsLeft = (int) (Math.random() * 15);
                sb.append(randomWord(1 + (int) (Math.random() * 3), true));
            } else {
                sentenceWordsLeft--;
                sb.append(randomWord(1 + (int) (Math.random() * 3), false));
                if (words > 0 && sentenceWordsLeft > 2 && Math.random() < 0.2) {
                    sb.append(',');
                } else if (sentenceWordsLeft == 0 || words == 0) {
                    sb.append('.');
                }
            }
        }
        return sb.toString();
    }

    static String randomName() {
        int len = (int) (Math.random() * 4) + 1;
        return randomWord(len, true);
    }

    static String randomTitle(int words) {
        StringBuffer sb = new StringBuffer();
        int len = (int) (Math.random() * 4) + 1;
        sb.append(randomWord(len, true));
        while (--words > 0) {
            len = (int) (Math.random() * 4) + 1;
            sb.append(' ');
            sb.append(randomWord(len, false));
        }
        return sb.toString();
    }

    static String randomHTML(int words) {
        StringBuffer sb = new StringBuffer();
        while (words > 0) {
            sb.append("<h2>");
            int len = (int) (Math.random() * 4) + 1;
            sb.append(randomTitle(len));
            sb.append("</h2>");
            words -= len;
            int paragraphs = 1 + (int) (Math.random() * 3);
            while (paragraphs-- > 0 && words > 0) {
                sb.append("<p>");
                len = (int) (Math.random() * 40) + 3;
                sb.append(randomText(len));
                sb.append("</p>");
                words -= len;
            }
        }
        return sb.toString();
    }

    static Collection<DashboardNotification> randomNotifications() {
        DashboardNotification n1 = new DashboardNotification();
        n1.setId(1);
        n1.setFirstName(randomFirstName());
        n1.setLastName(randomLastName());
        n1.setAction("created a new report");
        n1.setPrettyTime("25 minutes ago");
        n1.setContent(randomText(18));

        DashboardNotification n2 = new DashboardNotification();
        n2.setId(2);
        n2.setFirstName(randomFirstName());
        n2.setLastName(randomLastName());
        n2.setAction("changed the schedule");
        n2.setPrettyTime("2 days ago");
        n2.setContent(randomText(10));

        return Arrays.asList(n1, n2);
    }

    public static int[] randomSparklineValues(int howMany, int min, int max) {
        int[] values = new int[howMany];

        for (int i = 0; i < howMany; i++) {
            values[i] = (int) (min + (Math.random() * (max - min)));
        }

        return values;
    }

}