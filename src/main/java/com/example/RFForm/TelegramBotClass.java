package com.example.RFForm;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class TelegramBotClass extends TelegramLongPollingBot {
    String BOT_TOKEN;
    String BOT_USERNAME;
    String temp;
    int room1 = 3, room2 = 3, room3 = 3, room4 = 3, room5 = 3, room6 = 3;

    TelegramBotClass(@Value("${bot.BOT_TOKEN}") String BOT_TOKEN, @Value("${bot.BOT_USERNAME}") String BOT_USERNAME) {
        this.BOT_TOKEN = BOT_TOKEN;
        this.BOT_USERNAME = BOT_USERNAME;
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand("/start", "Главная"));
        menu.add(new BotCommand("/rooms", "Номера"));
        menu.add(new BotCommand("/prices", "Цены"));
        menu.add(new BotCommand("/restaurant", "Рестораны"));
        menu.add(new BotCommand("/conference", "Конференц услуги"));
        menu.add(new BotCommand("/sports", "Спорт"));
        menu.add(new BotCommand("/contact", "Контакты"));
        menu.add(new BotCommand("/map", "Карта"));
        try {
            this.execute(new SetMyCommands(menu, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("error");
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getChatId() == 667621439L) {
                try {
                    execute(confirmMessage(update));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (update.getMessage().getText().equals("/start")) {
                    mainPage(update);
                } else if (update.getMessage().getText().equals("/rooms")) {
                    try {
                        execute(roomPage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (update.getMessage().getText().equals("/prices")) {
                    try {
                        execute(pricePage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (update.getMessage().getText().equals("/restaurant")) {
                    try {
                        execute(restaurantPage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (update.getMessage().getText().equals("/conference")) {
                    try {
                        execute(conferencePage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (update.getMessage().getText().equals("/sports")) {
                    sportPage(update);
                } else if (update.getMessage().getText().equals("/contact")) {
                    sendMessage("""
                            Кыргызская Республика, 722117, Иссыккульский район, с. Сары-Ой.
                                                    
                            Телефоны:
                            +996 772 320519 / Telegram, WhatsApp
                            +996 555 535390 / Telegram, WhatsApp
                            E-mail: karven@inbox.ru

                            По вопросам проведения Конференций, тренингов или корпоративных мероприятий, а также для коммерческих предложений:
                                                    
                            Для бронирования туристических групп и частных лиц:
                            E-mail: karven@inbox.ru""", update);
                } else if (update.getMessage().getText().equals("/map")) {
                    sendMessage("\n\nКарта проезда в Центр отдыха «Карвен Четыре Сезона»", update);
                    sendPhoto(update.getMessage().getChatId(), "https://karven.kg/images/about-us/map.jpg");
                    mapPage(update);
                } else if (update.getMessage().getText().equals("Отзыв")) {
                    try {
                        execute(ratePage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else if (update.getMessage().getText().equals("Заказать звонок")) {
                    getInfoAboutUser(update);
                } else if (update.getMessage().getText().equals("Бронировать")) {
                    try {
                        execute(bronPage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    toAdmin(update);
                }
            }

        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("33") ||
                    update.getCallbackQuery().getData().equals("66") || update.getCallbackQuery().getData().equals("99")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(update.getCallbackQuery().getData());
                sendMessage.setChatId(667621439L);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                sendMessage.setText("Спасибо!");
                sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (update.getCallbackQuery().getData().equals("Отправить")) {
                adminSend(temp);
                temp = "";
            } else {
                try {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText(checkPodMenu(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getData()));
                    sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null)).getFileId();
            getInfoAboutUser(update);
            sendPhoto(667621439, f_id);
            sendMessage("Подождите пока модератор не проверить.", update);
            try {
                execute(approvalPage()); //одобрение
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private SendMessage approvalPage() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Одобрить");
        inlineKeyboardButton1.setCallbackData("Одобрить");
        inlineKeyboardButton2.setText("Не одобрить");
        inlineKeyboardButton2.setCallbackData("Не одобрить");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(667621439L);
        sendMessage.setText("Одобрите или нет?");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private void getInfoAboutUser(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(Long.valueOf(667621439));
        message.setText(update.getMessage().getChatId() + " " + update.getMessage().getFrom().getFirstName()
                + " " + update.getMessage().getFrom().getUserName());
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapPage(Update update) {
        sendMessage("-Приблизительно-", update);
        sendPhoto(update.getMessage().getChatId(), "https://github.com/nazimaBeauty/photoCarven/blob/main/image16.png?raw=true");
        sendPhoto(update.getMessage().getChatId(), "https://github.com/nazimaBeauty/photoCarven/blob/main/image17.png?raw=true");
    }

    private SendMessage bronPage(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Одноместные");
        inlineKeyboardButton1.setCallbackData("ОдноместныйБрон");
        inlineKeyboardButton2.setText("Двухместный");
        inlineKeyboardButton2.setCallbackData("ДвухместныйБрон");
        inlineKeyboardButton3.setText("Трёхместный");
        inlineKeyboardButton3.setCallbackData("ТрёхместныйБрон");
        inlineKeyboardButton4.setText("Люкс семейный");
        inlineKeyboardButton4.setCallbackData("Люкс семейныйБрон");
        inlineKeyboardButton5.setText("Апартаменты+");
        inlineKeyboardButton5.setCallbackData("Апартаменты+Брон");
        inlineKeyboardButton6.setText("VIP+");
        inlineKeyboardButton6.setCallbackData("VIP+Брон");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);
        keyboardButtonsRow3.add(inlineKeyboardButton5);
        keyboardButtonsRow3.add(inlineKeyboardButton6);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Какой номер вы хотите?");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private void adminSend(String text) {
        String smg = readFromFile();
        String[] elephantList = smg.split(",");
        for (String s : elephantList) sendMessageByChatId(text, Long.parseLong(s));
    }

    private void sendMsg(String s, Update update) {
        SendMessage message = new SendMessage();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        KeyboardRow keyboardFourthRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Бронировать"));
        keyboardSecondRow.add(new KeyboardButton("Изменить язык"));
        keyboardSecondRow.add(new KeyboardButton("Заказать звонок"));
        keyboardSecondRow.add(new KeyboardButton("Отзыв"));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        keyboard.add(keyboardFourthRow);

        replyKeyboardMarkup.setKeyboard(keyboard);


        message.setChatId(update.getMessage().getChatId().toString());
        message.setReplyToMessageId(update.getMessage().getMessageId());
        message.setText(s);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String checkPodMenu(Long update, String data) {
        int temp;
        switch (data) {
            case "Одноместные":
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/odnomestnii/standart-2020-02.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/odnomestnii/standart-single-012018new.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/odnomestnii/standart-single-022018new.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/odnomestnii/standart-single-032018new.jpg");
                return dataPodMenu(1);
            case "Двухместный":
                sendPhoto(update, "https://www.karven.kg/cache/plg_lightimg/images.rooms-and-cottages.dvuhmestnii.standart-double-052018new_1024x768.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/dvuhmestnii/standart-double-022018new.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/odnomestnii/standart-2020-02.jpg");
                return dataPodMenu(2);
            case "Трёхместный":
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/trehmestnii/01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/trehmestnii/09.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/trehmestnii/05.jpg");
                return dataPodMenu(3);
            case "Люкс семейный":
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/lux-family-cottage/01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/lux-family-cottage/02.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/lux-family-cottage/03.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/lux-family-cottage/05.jpg");
                return dataPodMenu(4);
            case "Апартаменты+":
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/apartments-plus/apartments-plus-2020-01.JPG");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/apartments-plus/01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/apartments-plus/02.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/apartments-plus/03.jpg");
                return dataPodMenu(5);
            case "VIP+":
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/vip-plus-cottage/vip-plus-cottage-2020-01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/vip-plus-cottage/01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/vip-plus-cottage/01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/rooms-and-cottages/vip-plus-cottage/04.jpg");
                return dataPodMenu(6);
            case "Прейскурант цен":
                sendPhoto(update, "https://github.com/nazimaBeauty/photoCarven/blob/main/image.png?raw=true");
                return "!!!Примечание:\n" +
                        "Арендатор несет материальную ответственность за ущерб и порчу инвентаря согласно акту!!!";
            case "Специальные предложения":
                return "В категории нет материалов.";
            case "Правила бронирования":
                sendDocument(update);
                return "!!!Читайте внимательно!!!";
            case "Four":
                sendPhoto(update, "https://www.karven.kg/images/restaurants/four-seasons-club/11.jpg");
                sendPhoto(update, "https://www.karven.kg/images/restaurants/four-seasons-club/12.jpg");
                sendPhoto(update, "https://www.karven.kg/images/restaurants/four-seasons-club/01.jpg");
                sendPhoto(update, "https://www.karven.kg/images/restaurants/four-seasons-club/02.jpg");
                sendPhoto(update, "https://www.karven.kg/images/restaurants/four-seasons-club/03.jpg");
                sendPhoto(update, "https://www.karven.kg/images/restaurants/four-seasons-club/09.jpg");
                return dataPodMenu(7);
            case "Хуторок":
                sendPhoto(update, "https://karven.kg/images/restaurants/hutorok/hutorok-14.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/hutorok/hutorok-15.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/hutorok/06.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/hutorok/12.jpg");
                return dataPodMenu(8);
            case "GRILL":
                sendPhoto(update, "https://karven.kg/images/restaurants/grill-house-beach/01.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/grill-house-beach/11.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/grill-house-beach/12.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/grill-house-beach/13.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/grill-house-beach/03.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/grill-house-beach/10.jpg");
                return dataPodMenu(9);
            case "AQUA":
                sendPhoto(update, "https://karven.kg/images/restaurants/aqua-bar/05.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/aqua-bar/07.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/aqua-bar/08.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/aqua-bar/04.jpg");
                sendPhoto(update, "https://karven.kg/images/restaurants/aqua-bar/02.jpg");
                return dataPodMenu(10);
            case "50":
                sendPhoto(update, "https://karven.kg/images/conference-services/small-conference-hall/09.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/small-conference-hall/08.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/small-conference-hall/06.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/small-conference-hall/06.jpg");
                sendPhoto(update, "https://github.com/nazimaBeauty/photoCarven/blob/main/images/image13.png?raw=true");
                return "Свободное размещение столов и стульев в элегантном конференц-зале делает его универсальным и дает возможность организовать любые типы рассадки. Зал вмещает до 70 человек при рассадке в театральном стиле и до 50 человек при рассадке за круглым столом. Этот зал подходит для проведения мероприятий самой различной направленности – заседания, пресс-конференции, тренинги и переговоры, презентации и фуршеты.";
            case "350":
                sendPhoto(update, "https://karven.kg/images/conference-services/big-conference-hall/20.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/big-conference-hall/21.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/big-conference-hall/14.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/big-conference-hall/13.jpg");
                sendPhoto(update, "https://github.com/nazimaBeauty/photoCarven/blob/main/images/image14.png?raw=true");
                return "Огромный конференц-зал дает возможность проведения любых масштабных мероприятий – конференции, съезды, банкеты, свадьбы и любые торжества. Зал вмещает до 300 человек на банкет и до 400 человек при рассадке в театральном стиле.";
            case "vip":
                sendPhoto(update, "https://karven.kg/images/conference-services/vip-hall/03.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/vip-hall/04.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/vip-hall/04.jpg");
                sendPhoto(update, "https://karven.kg/images/conference-services/vip-hall/02.jpg");
                sendPhoto(update, "https://github.com/nazimaBeauty/photoCarven/blob/main/images/image15.png?raw=true");
                return "В роскошном VIP зале с камином можно проводить переговоры и приемы любого уровня в атмосфере максимального комфорта, уюта и высокого сервиса.";
            case "33":
                return "Спасибо! Но, могу-ли знать почему 0-33?";
            case "66":
                return "Спасибо! Но, могу-ли знать почему 33-66?";
            case "99":
                return "Спасибо! Но, могу-ли знать почему 66-99?";
            case "Отмена":
                return "Отменено";

            case "ОдноместныйБрон":
                temp = roomChecker(1, '-');
                if (temp > 0)
                    return "Пополните баланс на счет 1180000098542005 и отправьте чек или звоните на +996509020253";
                else return "Извините, пока все комнаты занята ( Может из других коттеджей хотите?";
            case "ДвухместныйБрон":
                temp = roomChecker(2, '-');
                if (temp > 0)
                    return "отправьте чек на счет 1180000098542005";
                else return "Извините, пока все комнаты занята ( Может из других коттеджей хотите?";
            case "ТрёхместныйБрон":
                temp = roomChecker(3, '-');
                if (temp > 0)
                    return "отправьте чек на счет 1180000098542005";
                else return "Извините, пока все комнаты занята ( Может из других коттеджей хотите?";
            case "Люкс семейныйБрон":
                temp = roomChecker(4, '-');
                if (temp > 0)
                    return "отправьте чек на счет 1180000098542005";
                else return "Извините, пока все комнаты занята ( Может из других коттеджей хотите?";
            case "Апартаменты+Брон":
                temp = roomChecker(5, '-');
                if (temp > 0)
                    return "отправьте чек на счет 1180000098542005";
                else return "Извините, пока все комнаты занята ( Может из других коттеджей хотите?";
            case "VIP+Брон":
                temp = roomChecker(6, '-');
                if (temp > 0)
                    return "отправьте чек на счет 1180000098542005";
                else return "Извините, пока все комнаты занята ( Может из других коттеджей хотите?";
            case "Одобрить":
                sendMessageByChatId("Одобрено", update);
                return new Date() + ", id: " + update;
            case "Не одобрить":
                roomChecker(6, '+');
                sendMessageByChatId("Не одобрено", update);
                return "Не одобрено";
            default:
                return "Not ready yet :( Sorry";
        }
    }

    private int roomChecker(int i, char ch) {
        if (ch == '-') {
            if (i == 1) {
                room1--;
                return room1;
            } else if (i == 2) {
                room2--;
                return room2;
            } else if (i == 3) {
                room3--;
                return room3;
            } else if (i == 4) {
                room4--;
                return room4;
            } else if (i == 5) {
                room5--;
                return room5;
            } else if (i == 6) {
                room6--;
                return room6;
            }
        } else if (ch == '+') {
            if (i == 1) {
                room1++;
                return room1;
            } else if (i == 2) {
                room2++;
                return room2;
            } else if (i == 3) {
                room3++;
                return room3;
            } else if (i == 4) {
                room4++;
                return room4;
            } else if (i == 5) {
                room5++;
                return room5;
            } else if (i == 6) {
                room6++;
                return room6;
            }
        }
        return 0;
    }

    private String dataPodMenu(int i) {
        if (i == 1) return """
                4 200 сом (МАЙ, ОКТЯБРЬ, НОЯБРЬ, ДЕКАБРЬ)
                4 640 сом (С 1-15 ИЮНЯ,С 15-30 СЕНТЯБРЯ)
                5 900 сом (С 15-30 ИЮНЯ, С 1-15 СЕНТЯБРЯ, ИЮЛЬ, АВГУСТ)
                                
                Стандартный одноместный номер располагается в двухэтажном корпусе, представляет собой однокомнатный номер с большой двуспальной кроватью, сан. узлом (душ-кабина), балконом с видом на озеро или на горы.
                                
                В стоимость номера входит: завтрак на одну персону.
                                
                В номерах имеется:
                    -Вместительный гардеробный шкаф
                    -Банные халаты и тапочкиКомфортабельное рабочее место с освещением
                    -Шампунь, кондиционер, гель для душа, мыло
                    -Телевизор
                    -Набор полотенец
                    -Высокоскоростной Интернет Wi-Fi (карточки с паролем для подключения к сети Wi-Fi приобретаются у администратора за отдельную плату)
                    -Мини-бар
                    -Электрочайник, посуда""";
        else if (i == 2) return """
                6 750 сом (МАЙ, ОКТЯБРЬ, НОЯБРЬ, ДЕКАБРЬ)
                7 400 сом (С 1-15 ИЮНЯ, С 15-30 СЕНТЯБРЯ)
                9 300 сом (С 15-30 ИЮНЯ, С 1-15 СЕНТЯБРЯ, ИЮЛЬ, АВГУСТ)
                                
                Стандартный двухместный номер располагается в двухэтажном корпусе, представляет собой однокомнатный номер с большой двуспальной кроватью или двумя полутораспальными кроватями, сан. узлом (душ-кабина), балконом с видом на озеро или на горы.
                                
                В стоимость номера входит: завтрак на две персоны.
                                
                В номерах имеется:
                    -Вместительный гардеробный шкаф
                    -Банные халаты и тапочки
                    -Комфортабельное рабочее место с освещением
                    -Шампунь, кондиционер, гель для душа, мыло
                    -Телевизор
                    -Набор полотенец
                    -Высокоскоростной Интернет Wi-Fi (карточки с паролем для подключения к сети Wi-Fi приобретаются у администратора за отдельную плату)
                    -Мини-бар
                    -Электрочайник, посуда
                """;
        else if (i == 3) return """
                10 200 сом (МАЙ, ОКТЯБРЬ, НОЯБРЬ, ДЕКАБРЬ)
                11 000 сом (С 1-15 ИЮНЯ, С 15-30 СЕНТЯБРЯ)
                12 800 сом (С 15-30 ИЮНЯ, С 1-15 СЕНТЯБРЯ, ИЮЛЬ, АВГУСТ)
                                
                Стандартный трехместный номер располагается в двухэтажном корпусе, представляет собой однокомнатный номер с тремя полутораспальными кроватями, сан. узлом (душ-кабина), балконом с видом на озеро или на горы.
                                
                В стоимость номера входит: завтрак на три персоны.
                                
                В номерах имеется:
                    -Вместительный гардеробный шкаф
                    -Банные халаты и тапочки
                    -Комфортабельное рабочее место с освещением
                    -Шампунь, кондиционер, гель для душа, мыло
                    -Телевизор
                    -Набор полотенец
                    -Высокоскоростной Интернет Wi-Fi (карточки с паролем для подключения к сети Wi-Fi приобретаются у администратора за отдельную плату)
                    -Мини-бар
                    -Электрочайник, посуда
                """;
        else if (i == 4) return """
                13 500 сом (МАЙ, ОКТЯБРЬ, НОЯБРЬ, ДЕКАБРЬ)
                15 200 сом (С 1-15 ИЮНЯ, С 15-30 СЕНТЯБРЯ)
                20 250 сом (С 15-30 ИЮНЯ, С 1-15 СЕНТЯБРЯ, ИЮЛЬ, АВГУСТ)
                                
                Уютный 3-х комнатный дом, состоящий из двух спальных комнат и гостиной-кухни. Отдельная ванная комната. Просторная гостиная с мягкой мебелью, телевизором и полностью оснащенной кухонной зоной с большим обеденным столом. Терраса с ротанговой мебелью, и специально отведенное парковочное место в непосредственной близи от парадного входа.
                                
                В стоимость номера входит: завтрак на четыре персоны.
                                
                В номерах имеется:
                    -Вместительный гардеробный шкаф
                    -Банные халаты и тапочки
                    -Комфортабельное рабочее место с освещением
                    -Шампунь, кондиционер, гель для душа, мыло
                    -Телевизор
                    -Набор полотенец
                    -Высокоскоростной Интернет Wi-Fi (карточки с паролем для подключения к сети Wi-Fi приобретаются у администратора за отдельную плату)
                    -Мини-бар
                    -Электрочайник, посуда
                """;
        else if (i == 5) return """
                15 200 сом (МАЙ, ОКТЯБРЬ, НОЯБРЬ, ДЕКАБРЬ)
                17 550 сом (С 1-15 ИЮНЯ, С 15-30 СЕНТЯБРЯ)
                25 300 сом (С 15-30 ИЮНЯ, С 1-15 СЕНТЯБРЯ, ИЮЛЬ, АВГУСТ)
                                
                Просторный 3-х комнатный коттедж: две спальные комнаты, гостиная-студия с камином, две ванные комнаты. Мягкий уголок у телевизора в гостиной, кухня, оснащенная микроволновой печью, холодильником и всей необходимой посудой, воссоздадут атмосферу домашнего уюта, сохранить который помогут теплые полы и камин.
                                
                Коттедж также имеет террасу с ротанговой мебелью, и специально отведенное парковочное место в непосредственной близи от парадного входа.
                                
                В стоимость номера входит: завтрак на четыре персоны.
                                
                В номерах имеется:
                    Вместительный гардеробный шкаф
                    Банные халаты и тапочки
                    Комфортабельное рабочее место с освещением
                    Шампунь, кондиционер, гель для душа, мыло
                    Телевизор
                    Набор полотенец
                    Высокоскоростной Интернет Wi-Fi (карточки с паролем для подключения к сети Wi-Fi приобретаются у администратора за отдельную плату)
                    Мини-бар
                    Электрочайник, посуда
                """;
        else if (i == 6)
            return """
                    19 660 сом (МАЙ, ОКТЯБРЬ, НОЯБРЬ, ДЕКАБРЬ)
                    24 100 сом (С 1-15 ИЮНЯ, С 15-30 СЕНТЯБРЯ)
                    42 200 сом (С 15-30 ИЮНЯ, С 1-15 СЕНТЯБРЯ, ИЮЛЬ, АВГУСТ)
                                        
                    Просторный 4-х комнатный коттедж с тремя спальными, тремя душевыми кабинами и двумя санузлами, в одной из которых для удобства клиентов установления стиральная машина-автомат. Большая гостиная-студия с камином, комплектом мягкой мебели и кухней, полностью укомплектованной всей необходимой посудой, холодильником, микроволновой печью, плитой для приготовления пищи и большим обеденным столом.С двух сторон коттеджа имеются террасы с ротанговой мебелью с великолепным видом на горы и озеро. Имеется парковочное место в непосредственной близи от парадного входа. Номер идеально подходит для семейного отдыха.
                                        
                    В стоимость номера входит: завтрак на шесть персон.
                                        
                    В номерах имеется:
                        Вместительный гардеробный шкаф
                        Банные халаты и тапочки
                        Комфортабельное рабочее место с освещением
                        Шампунь, кондиционер, гель для душа, мыло
                        Телевизор
                        Набор полотенец
                        Высокоскоростной Интернет Wi-Fi (карточки с паролем для подключения к сети Wi-Fi приобретаются у администратора за отдельную плату)
                        Мини-бар
                        Электрочайник, посуда
                    """;
        else if (i == 7) return """
                Часы работы:
                -08:00 - 23:00
                (после 23:00 в режиме ночного клуба)
                                
                -Завтрак Шведский стол 8.30-10.30
                                
                -Комплексный обед 13.00-14.30
                                
                -Комплексный ужин 19.00-20.00
                                
                -Домашний уют и лучший сервис, а также организация и проведение свадеб, банкетов, фуршетов, корпоративных вечеров и праздников:
                                
                -Телефон: +996 (559) 900444
                -E-mail: karven@inbox.ru
                                
                -Мы готовы взять на себя все мероприятие «под ключ»: сцена, звуковое и световое оборудование, шоу-программы.
                """;
        else if (i == 8) return """
                Часы работы:
                -10:00 – 23:30 с июня по август включительно
                                
                -Домашний уют и лучший сервис, а также организация и проведение свадеб, банкетов, фуршетов, корпоративных вечеров и праздников:
                                
                Телефон: +996 (559) 900444
                E-mail: karven@inbox.ru
                                
                -Мы готовы взять на себя все мероприятие «под ключ»: сцена, звуковое и световое оборудование, шоу-программы.
                """;
        else if (i == 9) return """
                Часы работы:
                -10:00 – 01:00 с мая по сентябрь включительно
                                
                -Домашний уют и лучший сервис, а также организация и проведение свадеб, банкетов, фуршетов, корпоративных вечеров и праздников:
                                
                Телефон: +996 (559) 900444
                E-mail: karven@inbox.ru
                                
                -Мы готовы взять на себя все мероприятие «под ключ»: сцена, звуковое и световое оборудование, шоу-программы.
                """;
        else if (i == 10) return """
                Часы работы:
                -09:00 – 20:00
                с 15 июня по 30 сентября
                                
                -Домашний уют и лучший сервис, а также организация и проведение свадеб, банкетов, фуршетов, корпоративных вечеров и праздников:
                                
                Телефон: +996 (559) 900444
                E-mail: karven@inbox.ru
                                
                -Мы готовы взять на себя все мероприятие «под ключ»: сцена, звуковое и световое оборудование, шоу-программы.
                """;

        return "";
    }

    private void toAdmin(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(Long.valueOf(667621439));
        getInfoAboutUser(update);
        message.setText(update.getMessage().getText());
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMessage("Скоро свяжемся, спасибо что выбрали нас)", update);
    }

    private SendMessage confirmMessage(Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Отправить");
        inlineKeyboardButton1.setCallbackData("Отправить");
        inlineKeyboardButton2.setText("Отмена");
        inlineKeyboardButton2.setCallbackData("Отмена");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Точно вы хотите отправить?");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        temp = update.getMessage().getText();
        return sendMessage;
    }

    private SendMessage ratePage(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("0-33");
        inlineKeyboardButton1.setCallbackData("33");
        inlineKeyboardButton2.setText("33-66");
        inlineKeyboardButton2.setCallbackData("66");
        inlineKeyboardButton3.setText("66-99");
        inlineKeyboardButton3.setCallbackData("99");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow1.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("На сколько вы оцениваете наш бот?");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private void sportPage(Update update) {
        sendPhoto(update.getMessage().getChatId(), "https://karven.kg/images/sport/02.jpg");
        sendPhoto(update.getMessage().getChatId(), "https://karven.kg/images/sport/04.jpg");
        sendPhoto(update.getMessage().getChatId(), "https://karven.kg/images/sport/05.jpg");
        sendPhoto(update.getMessage().getChatId(), "https://karven.kg/images/sport/08.jpg");
        sendMessage("Спорт – неотъемлемая и наиболее привлекательная часть жизни гостей нашего Центра Отдыха. Здесь созданы все условия для поддержания идеальной спортивной формы и ведения здорового образа жизни.\n" +
                "На современных, профессионально оснащенных спортивных площадках центра отдыха «Карвен Четыре Сезона» не раз проводились турниры и соревнования различного уровня и масштаба такие как: международные турниры по бильярду, боулингу, настольному и большому теннису среди профессионалов и любителей.", update);
    }

    private SendMessage conferencePage(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Конференц зал на 50-75 человек");
        inlineKeyboardButton1.setCallbackData("50");
        inlineKeyboardButton2.setText("Конференц зал на 350 человек");
        inlineKeyboardButton2.setCallbackData("350");
        inlineKeyboardButton3.setText("VIP зал");
        inlineKeyboardButton3.setCallbackData("vip");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Конференц услуги");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage restaurantPage(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Ресторан \"Four seasons Club\"");
        inlineKeyboardButton1.setCallbackData("Four");
        inlineKeyboardButton2.setText("«Хуторок» - cлавянский стилизованный летний ресторан");
        inlineKeyboardButton2.setCallbackData("Хуторок");
        inlineKeyboardButton3.setText("GRILL HOUSE BEACH");
        inlineKeyboardButton3.setCallbackData("GRILL");
        inlineKeyboardButton4.setText("Бар «AQUA BAR»");
        inlineKeyboardButton4.setCallbackData("AQUA");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Рестораны");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage pricePage(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Прейскурант цен");
        inlineKeyboardButton1.setCallbackData("Прейскурант цен");
        inlineKeyboardButton2.setText("Специальные предложения");
        inlineKeyboardButton2.setCallbackData("Специальные предложения");
        inlineKeyboardButton3.setText("Правила бронирования");
        inlineKeyboardButton3.setCallbackData("Правила бронирования");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Цены");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage roomPage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Одноместные");
        inlineKeyboardButton1.setCallbackData("Одноместные");
        inlineKeyboardButton2.setText("Двухместный");
        inlineKeyboardButton2.setCallbackData("Двухместный");
        inlineKeyboardButton3.setText("Трёхместный");
        inlineKeyboardButton3.setCallbackData("Трёхместный");
        inlineKeyboardButton4.setText("Люкс семейный");
        inlineKeyboardButton4.setCallbackData("Люкс семейный");
        inlineKeyboardButton5.setText("Апартаменты+");
        inlineKeyboardButton5.setCallbackData("Апартаменты+");
        inlineKeyboardButton6.setText("VIP+");
        inlineKeyboardButton6.setCallbackData("VIP+");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);
        keyboardButtonsRow3.add(inlineKeyboardButton5);
        keyboardButtonsRow3.add(inlineKeyboardButton6);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Номера");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private void mainPage(Update update) {
        writeToFile(update.getMessage().getChatId().toString());

        String message = """
                Наш центр отдыха расположен на берегу озера Иссык-Куль, в живописном местечке Сары-Ой (Золотая Долина)- в 244 км. езды от г. Бишкек и в 9 км от г. Чолпон Ата.

                «Карвен Четыре Сезона» - первоклассный отель, состоящий из стандартных номеров в корпусах и коттеджей от люкса до Президентского номера, рассчитанный разместить до 210 гостей. Отель имеет очень развитую инфраструктуру для полноценного отдыха, укрепления здоровья и проведения деловых мероприятий различного уровня все 365 дней в году!
                """;
        String link = "https://youtu.be/9lgbqxLn6e4";
        sendMsg("Добро пожаловать в Центр отдыха\n" +
                "«Карвен Четыре Сезона»\n " + "\n" + message + " " + link, update);
    }

    private void sendMessage(String s, Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setReplyToMessageId(update.getMessage().getMessageId());
        message.setText(s);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageByChatId(String msg, long chat) {
        SendMessage message = new SendMessage();
        message.setChatId(chat);
        message.setText(msg);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    private boolean checkingSpam(String text) {
//        String keyword = readFromFile();
//        boolean checker;
//        String[] words = keyword.split("\\s+");
//        for (int i = 0; i < words.length; i++) {
//            words[i] = words[i].replaceAll("\\W", "");
//            checker = Arrays.asList(text.split(",")).contains(words[i]);
//            if (checker) {
//                return true;
//            }
//        }
//        return false;
//    }
//
    private String readFromFile() {
        String text;
        try {
            FileInputStream fis = new FileInputStream("data.txt");
            text = IOUtils.toString(fis, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private void writeToFile(String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt", true));
            writer.append(',');
            writer.append(text);

            writer.close();
        } catch (IOException e) {
            System.out.println("error");
        }
    }

    private void sendPhoto(long chat_id, String s) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chat_id);
        InputFile inputFile = new InputFile(s);
        sendPhoto.setPhoto(inputFile);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocument(Long update) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(update);
        InputFile inputFile = new InputFile("http://karven.kg/images/news/2018/05/booking-order-rules.pdf");
        sendDocument.setDocument(inputFile);
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
