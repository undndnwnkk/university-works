#include <iostream>
#include <vector>

using namespace std;

int main() {
  int n;
  cin >> n;

  vector<int> queue;

  for (int i = 0; i < n; i++) {
    char action;
    cin >> action;

    if (action == '+' || action == '*') {
      int number;
      cin >> number;

      if (action == '+') {
        queue.push_back(number);
      } else if (action == '*') {
        int pos = (queue.size() + 1) / 2;
        queue.insert(queue.begin() + pos, number);
      }
    } else if (action == '-') {
      cout << queue[0] << endl;
      queue.erase(queue.begin());
    }
  }

  return 0;
}