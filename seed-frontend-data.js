const { Builder, By, Key, until, Select } = require('selenium-webdriver');

async function seedRealData() {
    let driver = await new Builder().forBrowser('chrome').build();
    const baseUrl = 'http://localhost:8080/api'; 
    const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

    // Hàm helper để xử lý Alert (nếu có)
    const handleAlert = async () => {
        try {
            await driver.wait(until.alertIsPresent(), 2000);
            let alert = await driver.switchTo().alert();
            console.log(`  🔔 Thông báo hệ thống: ${await alert.getText()}`);
            await alert.accept();
        } catch (e) {
            // Không có alert thì thôi, bỏ qua
        }
    };

    // Hàm helper để click an toàn (cuộn chuột và click bằng JS nếu cần)
    const clickSafe = async (element) => {
        await driver.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        await sleep(500);
        try {
            await element.click();
        } catch (e) {
            await driver.executeScript("arguments[0].click();", element);
        }
    };

    try {
        console.log('🚀 BẮT ĐẦU SEED DỮ LIỆU THỰC TẾ (Hệ thống đào tạo) 🚀\n');

        // --- 1. SEED THANG ĐIỂM (5 Mức chuẩn) ---
        console.log('>>> Đang tạo Thang điểm (A, B, C, D, F)...');
        const scales = [
            { code: 'A', letter: 'A', min: 8.5, max: 10.0, gpa: 4.0 },
            { code: 'B', letter: 'B', min: 7.0, max: 8.4, gpa: 3.0 },
            { code: 'C', letter: 'C', min: 5.5, max: 6.9, gpa: 2.0 },
            { code: 'D', letter: 'D', min: 4.0, max: 5.4, gpa: 1.0 },
            { code: 'F', letter: 'F', min: 0.0, max: 3.9, gpa: 0.0 }
        ];

        for (let s of scales) {
            await driver.get(`${baseUrl}/grade-scales`);
            await sleep(1000);
            // Bấm nút Thêm (giống file test cũ của em)
            let btnAddScale = await driver.wait(until.elementLocated(By.partialLinkText('Thêm')), 5000);
            await clickSafe(btnAddScale);
            
            await driver.wait(until.elementLocated(By.name('scaleCode')), 5000);
            
            await driver.findElement(By.name('scaleCode')).sendKeys(s.code);
            await driver.findElement(By.name('letterGrade')).sendKeys(s.letter);
            await driver.findElement(By.name('minScore')).sendKeys(s.min.toString());
            await driver.findElement(By.name('maxScore')).sendKeys(s.max.toString());
            await driver.findElement(By.name('gpaValue')).sendKeys(s.gpa.toString());
            
            let btnSubmitScale = await driver.findElement(By.css('button[type="submit"]'));
            await clickSafe(btnSubmitScale);
            await handleAlert(); // Tự động bấm OK
            await driver.wait(until.urlContains('/grade-scales'), 5000);
            console.log(`  ✅ Đã tạo Thang điểm: ${s.letter}`);
            await sleep(500);
        }

        // --- 2. SEED THÀNH PHẦN ĐIỂM (Bộ 3 chuẩn) ---
        console.log('\n>>> Đang tạo bộ Thành phần điểm (10% - 30% - 60%)...');
        const components = [
            { code: 'CC_01', name: 'Điểm Chuyên cần', weight: 10 },
            { code: 'GK_01', name: 'Kiểm tra Giữa kỳ', weight: 30 },
            { code: 'CK_01', name: 'Thi Cuối kỳ', weight: 60 }
        ];

        for (let c of components) {
            await driver.get(`${baseUrl}/grade-components`);
            await sleep(1000);
            // Bấm nút Thêm mới
            let btnAddComponent = await driver.wait(until.elementLocated(By.partialLinkText('Thêm mới')), 5000);
            await clickSafe(btnAddComponent);
            
            await driver.wait(until.elementLocated(By.name('componentName')), 5000);

            await driver.findElement(By.name('courseSectionId')).sendKeys('550e8400-e29b-41d4-a716-446655440000'); // UUID mẫu
            await driver.findElement(By.name('componentCode')).sendKeys(c.code);
            await driver.findElement(By.name('componentName')).sendKeys(c.name);
            
            let w = await driver.findElement(By.name('weightPercentage'));
            await w.clear(); await w.sendKeys(c.weight.toString());
            
            let btnSubmitComponent = await driver.findElement(By.css('button[type="submit"]'));
            await clickSafe(btnSubmitComponent);
            await handleAlert(); // Tự động bấm OK
            await driver.wait(until.urlContains('/grade-components'), 5000);
            console.log(`  ✅ Đã tạo Thành phần: ${c.name}`);
            await sleep(500);
        }

        // --- 3. SEED ĐIỂM SINH VIÊN (10 bản ghi ngẫu nhiên) ---
        console.log('\n>>> Đang nhập 10 Điểm sinh viên mẫu...');
        for (let i = 1; i <= 10; i++) {
            await driver.get(`${baseUrl}/student-grades`);
            await sleep(1000);
            // Bấm nút Thêm
            let btnAddGrade = await driver.wait(until.elementLocated(By.partialLinkText('Thêm')), 5000);
            await clickSafe(btnAddGrade);
            
            await driver.wait(until.elementLocated(By.name('score')), 5000);

            // Ghi chú: Nếu form của em bắt buộc nhập Student ID, hãy bỏ comment dòng dưới và sửa đúng tên field
            // await driver.findElement(By.name('studentId')).sendKeys('5085c178-a215-47ee-a605-456be5ee786e');
            
            // Chọn Thành phần điểm (chọn random 1 trong 3 cái vừa tạo)
            let selectComp = new Select(await driver.findElement(By.name('gradeComponentId')));
            let compIndex = Math.floor(Math.random() * 3) + 1; // 1, 2 hoặc 3
            await selectComp.selectByIndex(compIndex);

            // Nhập điểm ngẫu nhiên từ 4 đến 10
            let randomScore = (Math.random() * 6 + 4).toFixed(1);
            await driver.findElement(By.name('score')).sendKeys(randomScore);
            await driver.findElement(By.name('note')).sendKeys(`Dữ liệu mẫu - Lần nhập ${i}`);

            let btnSubmitGrade = await driver.findElement(By.css('button[type="submit"]'));
            await clickSafe(btnSubmitGrade);
            await handleAlert(); // Tự động bấm OK
            await driver.wait(until.urlContains('/student-grades'), 5000);
            console.log(`  ✅ [${i}/10] Đã nhập điểm: ${randomScore}`);
            await sleep(300);
        }

        console.log('\n🏆 HOÀN TẤT SEED TOÀN BỘ DỮ LIỆU HỆ THỐNG!');

    } catch (error) {
        console.error('❌ Lỗi khi seed dữ liệu:', error);
    } finally {
        setTimeout(async () => { await driver.quit(); }, 2000);
    }
}

seedRealData();
