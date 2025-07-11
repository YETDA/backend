package com.funding.backend.enums;

public enum ProvidingMethod {

        DOWNLOAD("파일 다운로드"),
        EMAIL("이메일 전송");

        private final String label;

        ProvidingMethod(String label) {
                this.label = label;
        }

        public String getLabel() {
                return label;
        }

}