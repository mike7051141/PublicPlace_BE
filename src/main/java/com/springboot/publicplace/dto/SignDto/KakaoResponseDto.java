package com.springboot.publicplace.dto.SignDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.context.annotation.Profile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class KakaoResponseDto {
    private String name;
    private String phoneNumber;
    private String email;
    private String profileUrl;
    private String nickname;
    private String gender;
    private String ageRange;
    //카카오 계정 정보
    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount;
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAccount {

        //프로필 정보 제공 동의 여부
        @JsonProperty("profile_needs_agreement")
        public Boolean isProfileAgree;

        //닉네임 제공 동의 여부
        @JsonProperty("profile_nickname_needs_agreement")
        public Boolean isNickNameAgree;

        //프로필 사진 제공 동의 여부
        @JsonProperty("profile_image_needs_agreement")
        public Boolean isProfileImageAgree;

        //사용자 프로필 정보
        @JsonProperty("profile")
        public Profile profile;

        //이름 제공 동의 여부
        @JsonProperty("name_needs_agreement")
        public Boolean isNameAgree;

        //카카오계정 이름
        @JsonProperty("name")
        public String name;

        //이메일 제공 동의 여부
        @JsonProperty("email_needs_agreement")
        public Boolean isEmailAgree;

        //이메일이 유효 여부
        // true : 유효한 이메일, false : 이메일이 다른 카카오 계정에 사용돼 만료
        @JsonProperty("is_email_valid")
        public Boolean isEmailValid;

        //이메일이 인증 여부
        //true : 인증된 이메일, false : 인증되지 않은 이메일
        @JsonProperty("is_email_verified")
        public Boolean isEmailVerified;

        //카카오계정 대표 이메일
        @JsonProperty("email")
        public String email;

        //연령대 제공 동의 여부
        @JsonProperty("age_range_needs_agreement")
        public Boolean isAgeAgree;

        //연령대
        //참고 https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
        @JsonProperty("age_range")
        public String ageRange;

        //출생 연도 제공 동의 여부
        @JsonProperty("birthyear_needs_agreement")
        public Boolean isBirthYearAgree;

        //출생 연도 (YYYY 형식)
        @JsonProperty("birthyear")
        public String birthYear;

        //생일 제공 동의 여부
        @JsonProperty("birthday_needs_agreement")
        public Boolean isBirthDayAgree;

        //생일 (MMDD 형식)
        @JsonProperty("birthday")
        public String birthDay;

        //생일 타입
        // SOLAR(양력) 혹은 LUNAR(음력)
        @JsonProperty("birthday_type")
        public String birthDayType;

        //성별 제공 동의 여부
        @JsonProperty("gender_needs_agreement")
        public Boolean isGenderAgree;

        //성별
        @JsonProperty("gender")
        public String gender;

        //전화번호 제공 동의 여부
        @JsonProperty("phone_number_needs_agreement")
        public Boolean isPhoneNumberAgree;



    }
}