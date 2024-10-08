# GRIP_TEAM_EPARI
이파리의 GRIP 프로젝트

## 프로젝트 소개

클라이밍장별 혼잡도 (사용가능인원 중 현재 사용인원)를 계산하여, 지도와 각 클라이밍장 페이지에 작성합니다.

해당 서비스를 통하여, 사용자들의 클라이밍장 이용에 대한 편의성을 증가시킬 수 있으며,

클라이밍을 즐겨하시는 분들의 소통 창구가 될 수 있는 플랫폼을 제작해 보았습니다.

## 개발 기간
- 2024.07.23 (화) ~ 2024.09.06 (금)
- 1차 아이디어톤 발표
- 중간 발표
- 1차 해커톤
- 2차 아이디어톤 발표
- 2차 해커톤

## 개발자 소개
- 강슬빈 : 팀장, Server 개발자, 프론트 디자인 (Post, Comment)
- 김병규 : Server 개발자, 프론트 디자인 (Map, Likes)
- 이종우 : Server 개발자, 프론트 디자인 (User, Search)
- 정다예 : Server 개발자, 프론트 디자인 (ClimbingGym)

## 개발 환경
- Version : Java 21
- IDE : IntelliJ
- Framework : SpringBoot
- ORM : JPA

## 주요 기능
- 클라이밍장별 혼잡도를 측정하여, 해당 혼잡도를 사용자들이 확인할 수 있도록 보여준다
  - 클라이밍장별 혼잡도는 사용가능 인원 중 현재 사용인원을 표시하는 방식으로 계산한다.
  - 혼잡도를 사용자들이 확인할 당시, 좀 더 편한 서비스 제공을 위하여 그래프를 이용한다.
  - 혼잡도를 사용자들이 확인할 때, 지도에서부터 확인 가능하도록 혼잡, 여유등으로 표시한다.

- 커뮤니케이션 기능
  - 게시판을 이용한 커뮤니케이션 기능을 활용한다.
  - 게시판은 전체게시판도 존재하지만, 클라이밍장 별 게시판을 놓아, 사용자들로 하여금, 클라이밍장 별로 커뮤니티를 형성할 수 있도록 돕늗다.

- 검색 기능
  - 어떤 단어를 검색하였을때, 클라이밍장, 게시판, 댓글 등등에서 해당 검색어를 이용하여 검색하여, 모든 종합적인 결과가 나올 수 있도록 만든다.
  - 한번에 모든 검색 결과가 나올 수 있도록 만듬에 따라, 사용자들에게 편의성을 제공합니다

- 메인 페이지에 위치한 지도
  - 지도를 메인페이지에 위치하게 함으로써, 직관적으로 클라이밍장의 위치를 확인할 수 있도록 돕습니다.

## 데이터구조
- ERD Cloud :
  ![ClimbingERD](https://github.com/xeulbn/GRIP_TEAM_EPARI/blob/develop/src/main/resources/static/ClimbingERD.png)

## 설계 구조도
- 이벤트 스토밍 활용
  ![이벤트 스토밍](https://github.com/xeulbn/GRIP_TEAM_EPARI/blob/develop/src/main/resources/static/%EC%9D%B4%EB%B2%A4%ED%8A%B8%20%EC%8A%A4%ED%86%A0%EB%B0%8D.jpg)

## API
- API 명세서 : https://docs.google.com/document/d/1JbEcocoiVqVlVAi9Su-9-3h0fa011mTc6FnlMPfs26c/edit?usp=sharing






