import sys, csv 


def first(n):
    return int(n[0])

def combine(x, y):
    result_map = {}
    list = [x, y]
    for data in list:
        for row in data:
            key = str(row[0])
            if key in result_map:
                inner_list = result_map.get(key)
                for i, r in enumerate(row):
                    if i > 0:
                        inner_list.append(r)
            else:
                result_map[key] = row

    result = []
    for items in result_map.values():
        result.append(items)
    return result



def write(fields, rows):
    with open("summary.csv", "w") as file:
        writer = csv.writer(file)
        writer.writerow(fields)
        writer.writerows(rows)

def process_file(filename):
    file = open(filename, "r")
    items = file.readlines()
    result = []
    for line in items:
        list = []
        for index, num in enumerate(line.split(",")):
            if index == 0 and num != "\n":
                list.append(int(num))
            elif "\n" in num:
                num = num.replace("\n", "")
                list.append(num)
            else:
                list.append(num)
        result.append(list)
    result = sorted(result, key=first)
    return result

def main():
    fields = ["Problem size", "time (basic)", "space (basic)", "time (efficient)", "space (efficient)"]
    file_one = sys.argv[1]
    file_two = sys.argv[2]

    file_one_result = process_file(file_one)
    file_two_result = process_file(file_two)

    summary = combine(file_one_result, file_two_result)
    write(fields, summary)


def test():
    result = process_file("basic_results.csv")
    print(result)

if __name__ == "__main__":
    main()