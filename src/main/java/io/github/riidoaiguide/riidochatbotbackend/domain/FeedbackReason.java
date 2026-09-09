package io.github.riidoaiguide.riidochatbotbackend.domain;

import java.util.Arrays;
import java.util.List;

/**
 * 좋아요·싫어요를 누른 뒤 고르는 상세사유. 고르지 않고 창을 닫을 수 있으므로 없을 수도 있다.
 *
 * <p>화면 문구(label)는 여기서만 들고 있는다 — 저장되는 값은 코드다.
 * 문구를 다듬어도 이미 쌓인 평가와 어긋나지 않게 하기 위함이다.
 *
 * <p>각 사유는 자기가 어느 평가에 속하는지(rating) 알고 있다. 좋아요에 싫어요 사유를
 * 붙이는 요청을 서버가 걸러낼 수 있다.
 */
public enum FeedbackReason {

    ACCURATE(FeedbackRating.GOOD, "정확한 정보예요"),
    EASY_TO_UNDERSTAND(FeedbackRating.GOOD, "바로 이해됐어요"),
    WANTED_ANSWER(FeedbackRating.GOOD, "원하던 답이에요"),
    EASY_TO_FOLLOW(FeedbackRating.GOOD, "따라 하기 쉬워요"),
    SUFFICIENT(FeedbackRating.GOOD, "설명이 충분해요"),
    USEFUL_LINK(FeedbackRating.GOOD, "링크가 유용해요"),

    OFF_TOPIC(FeedbackRating.BAD, "질문과 달라요"),
    INACCURATE(FeedbackRating.BAD, "정보가 틀려요"),
    NOT_FOUND(FeedbackRating.BAD, "찾는 내용이 없어요"),
    INSUFFICIENT(FeedbackRating.BAD, "설명이 부족해요"),
    TOO_DIFFICULT(FeedbackRating.BAD, "어려워요"),
    BROKEN_LINK(FeedbackRating.BAD, "링크가 잘못됐어요");

    private final FeedbackRating rating;
    private final String label;

    FeedbackReason(FeedbackRating rating, String label) {
        this.rating = rating;
        this.label = label;
    }

    /** 해당 평가에서 고를 수 있는 사유들. 화면에 뿌리는 순서가 곧 선언 순서다. */
    public static List<FeedbackReason> of(FeedbackRating rating) {
        return Arrays.stream(values()).filter(reason -> reason.rating == rating).toList();
    }

    public FeedbackRating getRating() {
        return rating;
    }

    public String getLabel() {
        return label;
    }
}
