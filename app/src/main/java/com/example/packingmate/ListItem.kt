package com.example.packingmate

//여려 데이터를 끌고 다닐 모델 클래스
/*
CREATE TABLE listItem (
    item_id INT NOT NULL AUTO_INCREMENT,         -- 물품 고유 ID
    info_id INT NOT NULL,                        -- 연결된 여행 정보 ID
    //item_ckstate TINYINT(1) NOT NULL DEFAULT 0,  -- 체크 여부 (0: 미체크, 1: 체크됨) => 이걸 따로 저장할건가?
    item_name VARCHAR(100) NOT NULL,             -- 물품 이름
    item_plane ENUM('기내 필수', '기내 권장', '기내/위탁 가능', '위탁 필수') NOT NULL, -- 기내/위탁 여부
    is_custom TINYINT(1) NOT NULL DEFAULT 0,     --  사용자 커스텀 항목 여부 (필요함!)
    PRIMARY KEY (item_id),
    FOREIGN KEY (info_id) REFERENCES tripInfo(info_id)
);
*/
data class ListItem(
    val itemId : Int,
    val infoId: Int,
    var isChecked: Boolean,
    val itemName: String,
    val itemPlane: String,
    val isCustom: Boolean
) {
    companion object {
        val dummyList = listOf(
            ListItem(1, 1, false, "여권", "기내 필수", false),
            ListItem(2, 1, false, "충전기", "기내 권장", false),
            ListItem(3, 1, false, "선크림", "기내/위탁 가능", true),
            ListItem(4, 1, false, "면도기", "위탁 필수", false),
        )
    }
}