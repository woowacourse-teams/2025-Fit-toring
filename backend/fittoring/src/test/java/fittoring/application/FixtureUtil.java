package fittoring.application;

import fittoring.domain.model.*;
import fittoring.domain.model.password.Password;

public class FixtureUtil {
    public static Member getTestMember(){
        return new Member(
                "loginId",
                "MALE",
                "이름",
                new Phone("010-1234-5678"),
                Password.from("password"));
    }

    public static Member getTestAdmin(){
        return new Member(
                "adminId",
                "FEMALE",
                "관리자",
                new Phone("010-9876-5432"),
                Password.from("password"),
                MemberRole.ADMIN
        );
    }

    public static Mentoring getTestMentoring(Member member){
        member.registerAsMentor();
        return new Mentoring(
                member,
                5000,
                5,
                "content",
                "introduction",
                "https://chatRoomUrl"
        );
    }

    public static Certificate getTestCertificate(Mentoring mentoring){
        return new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
    }

}
