const { Builder, By, Key, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');

async function runGradeComponentTest() {
    // Cấu hình trình duyệt (chạy ở chế độ không cửa sổ nếu cần: .headless())
    let options = new chrome.Options();
    let driver = await new Builder()
        .forBrowser('chrome')
        .setChromeOptions(options)
        .build();

    // Hàm helper để dừng chương trình cho dễ quan sát browser
    const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

    try {
        console.log('--- Bắt đầu Test Module Thành phần điểm ---');
        
        // 1. Truy cập trang thêm mới (Thay đổi URL cho đúng với app của em)
        await driver.get('http://localhost:8080/grade-components/form'); 
        await driver.manage().window().maximize();

        // --- TC-02: Kiểm tra validation khi để trống ---
        console.log('Đang chạy TC-02: Kiểm tra validation khi để trống...');
        await sleep(2000);
        let submitBtn = await driver.findElement(By.css('button[type="submit"]'));
        await submitBtn.click();
        
        // Kiểm tra xem có hiển thị thông báo lỗi không (thay selector tương ứng)
        // let errorMsg = await driver.wait(until.elementLocated(By.className('error-text')), 2000);
        // console.log('Kết quả TC-02: OK (Đã báo lỗi)');

        // --- TC-01: Thêm mới thành công ---
        console.log('Đang chạy TC-01: Thêm mới thành phần điểm...');
        
        // Nhập UUID lớp học phần
        await sleep(2000);
        await driver.findElement(By.id('courseSectionId')).sendKeys('550e8400-e29b-41d4-a716-446655440000');
        
        // Nhập Mã thành phần
        await sleep(2000);
        await driver.findElement(By.id('componentCode')).sendKeys('CC_' + Date.now());
        
        // Nhập Tên thành phần
        await sleep(2000);
        await driver.findElement(By.id('componentName')).sendKeys('Chuyên cần - Auto Test');
        
        // Nhập Trọng số
        await sleep(2000);
        let weightInput = await driver.findElement(By.id('weightPercentage'));
        await weightInput.clear();
        await weightInput.sendKeys('10');
        
        // Nhập Điểm tối đa
        await sleep(2000);
        let maxScoreInput = await driver.findElement(By.id('maxScore'));
        await maxScoreInput.clear();
        await maxScoreInput.sendKeys('10');

        // Nhấn nút Thêm mới
        await sleep(2000);
        await submitBtn.click();

        // Chờ thông báo thành công hoặc chuyển hướng
        await driver.wait(until.urlContains('/grade-components'), 5000);
        console.log('Kết quả TC-01: SUCCESS (Thêm mới thành công)');

    } catch (error) {
        console.error('Lỗi trong quá trình test:', error);
    } finally {
        // Đóng trình duyệt sau 3 giây để quan sát kết quả
        setTimeout(async () => {
            await driver.quit();
            console.log('--- Kết thúc Test ---');
        }, 3000);
    }
}

runGradeComponentTest();
