package com.nextweb.nwapplogger.model

import kotlin.reflect.full.memberProperties

/**
 * @author nextweb
 *
 */
data class AppInfo constructor(
    var ct: String = "", // 로그생성시간
    var st: String = "", // 로그전송시간
    var osType: String = "", // OS 종류
    var osVersion: String = "", // OS 버전
    var appName: String = "", // 앱이름
    var activityName: String = "", // 액티비티명
    var packageName: String = "", // 패키지명
    var className: String = "", // 클래스명
    var xtVid: String = "", // ADID
    var xtDid: String = "", // UUID
    var xtUid: String = "", // 서비스 로그인 아이디
    var ssId: String = "", // 세션 아이디
    var evtType: String = "", // 이벤트 형태
    var evtDesc: String = "", // 이벤트 설명
    var cateId: String = "", // 카테고리

    var macAddress: String = "", // 맥주소
    var resolution: String = "", // 해상도
    var appRunCount: String = "", // 앱실행 횟수
    var referrer: String = "", // referrer

    var buildBoard: String = "",
    var buildBrand: String = "",
    var buildDevice: String = "",
    var buildDisplay: String = "",
    var buildFingerprint: String = "",
    var buildHost: String = "",
    var buildId: String = "",
    var buildManufacturer: String = "",
    var buildModel: String = "",
    var buildProduct: String = "",
    var buildSerial: String = "",
    var buildTags: String = "",
    var buildTime: String = "",
    var buildType: String = "",
    var buildUser: String = "",
    var buildVersionRelease: String = "",
    var buildVersionSdkInt: String = "",
    var buildVersionCodename: String = "",
    var buildHardware: String = "",
    var countryCd: String = "", // 국가코드
    var language: String = "", // 언어
    var carrier: String = "" // 통신사
) {
    fun asMap(): Map<String, Any?> = AppInfo::class.memberProperties.associate { it.name to it.get(this)}
}

