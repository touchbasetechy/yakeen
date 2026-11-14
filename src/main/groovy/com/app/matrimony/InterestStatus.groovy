package com.app.matrimony

enum InterestStatus {
  PENDING('pending'),
  ACCEPTED('accepted'),
  DECLINED('declined'),
  WITHDRAWN('withdrawn')

  final String value

  InterestStatus(String value) {
    this.value = value
  }

  String toString() {
    return value
  }
}
