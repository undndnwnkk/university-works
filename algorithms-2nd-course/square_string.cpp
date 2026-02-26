#include <iostream>
#include <string>
#include <vector>

int main() {
  int n;
  std::cin >> n;
  std::vector<std::string> lines(n);

  for (int i = 0; i < n; i++) {
    std::string str;
    std::cin >> str;
    lines[i] = str;
  }

  for (int i = 0; i < n; i++) {
    if (lines[i].size() % 2 != 0) {
      std::cout << "NO" << std::endl;
      continue;
    }

    int half = lines[i].size() / 2;
    bool is_true = true;

    for (int l = 0; l < half; l++) {
      if (lines[i][l] != lines[i][l + half]) {
        is_true = false;
        break;
      }
    }

    if (is_true) {
      std::cout << "YES" << std::endl;
    } else {
      std::cout << "NO" << std::endl;
    }
  }

  return 0;
}